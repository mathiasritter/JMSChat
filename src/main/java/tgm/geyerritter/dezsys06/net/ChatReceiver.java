package tgm.geyerritter.dezsys06.net;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tgm.geyerritter.dezsys06.data.MessageData;

/**
 * Implementierung von {@link Receiver}
 * 
 * @author mritter
 * @autor sgeyer
 * @version 1.0
 */
public class ChatReceiver implements Receiver {

	private MessageConsumer consumer;
	private Connection connection;
	private boolean run;
	private Session session;
	private static final Logger logger = LogManager
			.getLogger(ChatReceiver.class);
	
	public ChatReceiver(ConnectionFactory connectionFactory, String chatroom) throws JMSException {
		
		this.connection = connectionFactory.createConnection();
		connection.start();

		// Create the session
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(chatroom);
		
		//Create the consumer
		this.consumer = session.createConsumer(destination);
		
		this.run = true;

	}

	@Override
	public void run() {
		//Empfangen von Chatmessages
		while (run) {
			try {
				//Empfangen einer Chatmessage (blockierende Methode)
				ObjectMessage message = (ObjectMessage) consumer.receive();
				if (message != null) {
					//Schreiben der Chatmessages in die Konsole
					MessageData md = (MessageData) message.getObject();
					
					logger.info("[" + md.getCreationDate() + "] "
							+ md.getSender() + ": " + md.getContent());

					//Empfang bestaetigen
					message.acknowledge();
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see Receiver#getMails(String)
	 */
	@Override
	public void getMails(String username) throws JMSException {
		//Destination mit Queue des Users wird erstellt
		Destination privateDestination = session.createQueue(username);
		
		//Consumer mit der oben definierten Destination wird erstellt
		MessageConsumer privateConsumer = session
				.createConsumer(privateDestination);

		logger.info("*** Begin of Private Messages ***");

		//Abfragen einer Privatnachricht.
		//Durch receiveNoWait wird nicht blockiert.
		//Es wird eine Message vom Broker empfangen, falls eine vorhanden ist. Ansonsten ist Message null.
		ObjectMessage message = (ObjectMessage) privateConsumer.receiveNoWait();
		
		//Solange Message nicht null ist, diese auslesen und weitere empfangen.
		while (message != null) {

			//Chatmessage auslesen und in die Konsole schreiben
			MessageData md = (MessageData) message.getObject();

			logger.info("[" + md.getCreationDate() + "] " + md.getSender()
					+ ": " + md.getContent());

			//Empfang bestaetigen
			message.acknowledge();

			//Versuch, weitere Message zu empfangen
			message = (ObjectMessage) privateConsumer.receiveNoWait();
		}

		logger.info("*** End of Private Messages ***");
	}

	/**
	 * @see Receiver#stop
	 */
	@Override
	public void stop() throws JMSException {
		this.run = false;
		this.consumer.close();
		this.session.close();
		this.connection.close();
	}

}
