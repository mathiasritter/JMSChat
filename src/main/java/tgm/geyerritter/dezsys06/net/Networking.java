package tgm.geyerritter.dezsys06.net;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

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
	
	private Receiver reciever;
	private Sender sender;
	
	public Networking(String username, Configuration conf) {
		
		this.username = username;
		
		try {
			init(conf);
		} catch (JMSException e) {
			System.out.println("EKZEPTSCHON beim Verbinden zum Broker (falsche Adresse)");
			e.printStackTrace();
		}
		
		
//		this.reciever = new ChatReceiver(this.session, this.consumer);
//		this.sender = new ChatSender(this.session, this.producer);
	}
	
	/**
	 * @see NetworkController#init(Configuration)
	 */
	public void init(Configuration conf) throws JMSException {
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				conf.getUser(), conf.getPassword(), conf.getHostAddress());
		
		this.reciever = new ChatReceiver(connectionFactory, conf.getSystemName());
		this.sender = new ChatSender(connectionFactory, conf.getSystemName());

		new Thread(this.reciever).start();
		
//		connection = connectionFactory.createConnection();
//		connection.start();
//
//		// Create the session
//		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//		destination = session.createTopic(conf.getSystemName());
//
//		// Create the consumer
//		consumer = session.createConsumer(destination);
//
//		// Create a producer
//		producer = session.createProducer(destination);
	}
	
	/**
	 * @see NetworkController#halt()
	 */
	public void halt() {
		try {
			this.sender.stop();
			this.reciever.stop();
			
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

}
