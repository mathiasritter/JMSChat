package tgm.geyerritter.dezsys06.net;

import org.apache.activemq.ActiveMQConnectionFactory;
import tgm.geyerritter.dezsys06.data.Configuration;

import javax.jms.Connection;
import javax.jms.JMSException;
import java.util.List;

/**
 * 
 * Implementierung von {@link NetworkController} wobei das Networking von ActiveMQ benutzt wird
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public class Networking implements NetworkController {
	
	private String username;
	
	private Receiver reciever;
	private Sender sender;

	private Connection connection;

    private Configuration configuration;
	
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
		
		//Connection aufbauen
		this.connection = connectionFactory.createConnection();
		this.connection.start();
		
		//Receiver und Sender initialisieren
		this.reciever = new ChatReceiver(this.connection, conf.getSystemName(), this.username);
		this.sender = new ChatSender(this.connection, conf.getSystemName());

		//Receiver-Thread starten
		new Thread(this.reciever).start();
	}
	
	/**
	 * @see NetworkController#halt()
	 */
	public void halt() {
		try {
			this.sender.stop();
			this.reciever.stop();
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
	public void mail(String reciever, String message) {
		try {
			this.sender.mail(this.username, reciever, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see NetworkController#getMails()
	 */
	public void getMails() {
		try {
			this.reciever.getMails(this.username);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

    public String getIP() {
        return this.configuration.getHostAddress().substring(
                this.configuration.getHostAddress().lastIndexOf("/") + 1,
                this.configuration.getHostAddress().length()
        );
    }

    public String getChatroom() {
        return this.configuration.getSystemName();
    }

}
