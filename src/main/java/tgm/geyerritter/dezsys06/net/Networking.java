package tgm.geyerritter.dezsys06.net;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import tgm.geyerritter.dezsys06.data.Configuration;

/**
 * 
 * Implementierung von {@link Controller} wobei das Networking von ActiveMQ benutzt wird
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public class Networking implements NetworkController {
	
	private String username;
	
	private Session session;
	private Connection connection;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Destination destination;
	
	private Receiver reciever;
	private Sender sender;
	
	public Networking(String username, Configuration conf) {
		try {
			init(conf);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		this.username = username;
		
		this.reciever = new ChatReceiver(this.session, this.consumer);
		this.sender = new ChatSender(this.session, this.producer);
	}
	
	/**
	 * @see NetworkController#init(Configuration)
	 */
	public void init(Configuration conf) throws JMSException {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(conf.getUser(), conf.getPassword(), conf.getHostAddress());
			
			try {
				connection = connectionFactory.createConnection();
				connection.start();
				
				// Create the session
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				destination = session.createTopic(conf.getSystemName());

				// Create the consumer
				consumer = session.createConsumer(destination);

				// Create a producer
				producer = session.createProducer(destination);
			} catch (JMSException e) {
				System.out.println("EKZEPTSCHON beim Verbinden zum Broker (falsche Adresse)");
			}
	}
	
	/**
	 * @see NetworkController#halt()
	 */
	public void halt() {
		try {
			this.consumer.close();
			this.session.close();
			
			this.connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
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

}
