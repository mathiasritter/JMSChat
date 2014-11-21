package tgm.geyerritter.dezsys06.net;

import javax.jms.Connection;
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

	private Connection connection;
	
	public Networking(String username, Configuration conf) throws JMSException {
		
		this.username = username;
		
		init(conf);
		
	}
	
	/**
	 * @see NetworkController#init(Configuration)
	 */
	public void init(Configuration conf) throws JMSException {
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				conf.getUser(), conf.getPassword(), conf.getHostAddress());
		
		this.connection = connectionFactory.createConnection();
		connection.start();
		
		this.reciever = new ChatReceiver(this.connection, conf.getSystemName(), this.username);
		this.sender = new ChatSender(this.connection, conf.getSystemName());

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

}
