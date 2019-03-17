package tgm.geyerritter.dezsys06.net;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import javax.jms.JMSException;
import java.util.List;
import java.util.Set;

/**
 * 
 * Implementierung von {@link NetworkController} wobei das Networking von ActiveMQ benutzt wird
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public class Networking implements NetworkController {
	
	private String username;
	
	private Receiver receiver;
	private Sender sender;

	private ActiveMQConnection connection;

    private Configuration configuration;

    public static final Logger logger = LogManager.getLogger(Networking.class);
	
	public Networking(String username, Configuration conf) throws JMSException {
		
		this.username = username;
		
		init(conf);
		
	}
	
	/**
	 * @see NetworkController#init(Configuration)
	 */
	public void init(Configuration conf) throws JMSException {

        this.configuration = conf;
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				conf.getUser(), conf.getPassword(), conf.getHostAddress());


		connectionFactory.setTrustedPackages(List.of("tgm.geyerritter.dezsys06.data", "java.util"));

        this.connection = (ActiveMQConnection) connectionFactory.createConnection();
		this.connection.start();

        if (this.userIsOnline(this.username))
            throw new JMSException("Username " + this.username + " is already in use. Please choose another username.");

		//Receiver und Sender initialisieren
		this.receiver = new ChatReceiver(this.connection, conf.getSystemName(), this.username);
		this.sender = new ChatSender(this.connection, conf.getSystemName());

		//Receiver-Thread starten
		new Thread(this.receiver).start();
	}
	
	/**
	 * @see NetworkController#halt()
	 */
	public void halt() {
		try {
			this.sender.stop();
			this.receiver.stop();
			this.connection.close();
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		System.exit(0);
	}

	/**
	 * @see NetworkController#broadcast(String)
	 */
	public void broadcast(String message) {
		try {
			this.sender.broadcast(this.username, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see NetworkController#mail(String, String)
	 */
	public void mail(String receiver, String message) {
		try {
		    if (this.userIsOnline(receiver)) {
                this.sender.mail(this.username, receiver, message);
                logger.info("Mail sent!");
            } else {
                logger.error("Cannot send private message to user " + receiver + ". User does not exist.");
            }
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see NetworkController#getMails()
	 */
	public void getMails() {
		try {
			this.receiver.getMails(this.username);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

    public String getIP() {
        return this.configuration.getHostAddress().substring(
                this.configuration.getHostAddress().indexOf("//") + 2,
                this.configuration.getHostAddress().lastIndexOf(")")
        );
    }

    public String getChatroom() {
        return this.configuration.getSystemName();
    }

    private boolean userIsOnline(String username) throws JMSException {
        DestinationSource ds = connection.getDestinationSource();
        Set<ActiveMQQueue> queues = ds.getQueues();

        return queues.stream()
                .map(q -> {
                    try {
                        return q.getQueueName();
                    } catch (JMSException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .anyMatch(q -> q.equalsIgnoreCase(username));
    }

}
