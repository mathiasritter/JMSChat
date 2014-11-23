package tgm.geyerritter.dezsys06.net;

import java.text.SimpleDateFormat;

import javax.jms.Connection;
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
 * @author sgeyer
 * @version 1.0
 */
public class ChatReceiver implements Receiver {

	
	private boolean run;
	private Session session;
	private Session privateSession;
	private MessageConsumer consumer;
	private MessageConsumer privateConsumer;
	private static final Logger logger = LogManager
			.getLogger(ChatReceiver.class);
	
	/**
	 * Initialisieren des Chat-Receivers
	 * 
	 * @param connection bereits aufgebaute Connection zum Message-Broker
	 * @param chatroom Chatraum, in dem sich der User befindet
	 * @param username Username des Users
	 * @throws JMSException Fehler waehrend der Kommunikation
	 */
	public ChatReceiver(Connection connection, String chatroom, String username) throws JMSException {
		
		// Session erstellen
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(chatroom);
		
		// Consumer erstellen zum Empfangen der Nachrichten
		this.consumer = session.createConsumer(destination);
		
		
		/* Privatchat */
		
		//Private Session initialisieren
		this.privateSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//Destination mit Queue des Users wird erstellt
		Destination privateDestination = privateSession.createQueue(username);
						
		//Consumer mit der oben definierten Destination wird erstellt
		this.privateConsumer = privateSession.createConsumer(privateDestination);

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
					
					String timeStamp = new SimpleDateFormat("[dd.MM.yyyy - HH:mm:ss]").format(md.getCreationDate());
					
					logger.info(timeStamp + " " + md.getSender()
							+ ": " + md.getContent());

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
		
		logger.info("*** Begin of Private Messages ***");

		//Abfragen einer Privatnachricht.
		//Durch receiveNoWait wird nicht blockiert.
		//Es wird eine Message vom Broker empfangen, falls eine vorhanden ist. Ansonsten ist Message null.
		ObjectMessage message = (ObjectMessage) privateConsumer.receiveNoWait();
		
		//Solange Message nicht null ist, diese auslesen und weitere empfangen.
		while (message != null) {

			//Chatmessage auslesen und in die Konsole schreiben
			MessageData md = (MessageData) message.getObject();

			String timeStamp = new SimpleDateFormat("[dd.MM.yyyy - HH:mm:ss]").format(md.getCreationDate());
			
			logger.info(timeStamp + " " + md.getSender()
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
	}

}
