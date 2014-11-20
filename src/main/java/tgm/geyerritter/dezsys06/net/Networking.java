package tgm.geyerritter.dezsys06.net;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.MessageData;

public class Networking implements NetworkController {

	private Session session;
	private Connection connection;
	private MessageConsumer consumer;
	private MessageProducer producer;
	private Destination destination;
	
	public void init(Configuration conf) {
		try {

			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(conf.getUser(), conf.getPassword(), conf.getHostAddress());
			connection = connectionFactory.createConnection();
			connection.start();

			// Create the session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(conf.getSystemName());

			// Create the consumer
			consumer = session.createConsumer(destination);

			// Start receiving
			TextMessage message = (TextMessage) consumer.receive();
			if (message != null) {
				System.out.println("Message received: " + message.getText());
				message.acknowledge();
			}
			connection.stop();

		} catch (Exception e) {

			System.out.println("[MessageConsumer] Caught: " + e);
			e.printStackTrace();

		} finally {

			try {
				consumer.close();
			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
			try {
				connection.close();
			} catch (Exception e) {
			}

		}
	}

	public void broadcast(String message) {
		
	}

	public void mail(String reciever, String message) {

	}

	public List<MessageData> getMails() {
		return null;
	}

}
