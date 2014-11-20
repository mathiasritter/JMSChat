package tgm.geyerritter.dezsys06.net;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import tgm.geyerritter.dezsys06.data.ChatMessage;
import tgm.geyerritter.dezsys06.data.MessageData;

/**
 * Implementierung von {@link Sender}
 * 
 * @author mritter
 * @author sgeyer
 * @version 1.0
 */
public class ChatSender implements Sender {

	private MessageProducer producer;
	private Session session;
	private Connection connection;

//	/**
//	 * Initialisieren der Attribute im Konstruktor
//	 * 
//	 * @param session Aufgebaute Session zum Broker
//	 * @param producer Producer fuer ein Topic
//	 */
//	public ChatSender(Session session, MessageProducer producer) {
//
//		this.session = session;
//		this.producer = producer;
//
//	}
	
	
	public ChatSender(ConnectionFactory connectionFactory, String chatroom) throws JMSException {
		
		this.connection = connectionFactory.createConnection();
		connection.start();

		// Create the session
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(chatroom);
		
		//Create the producer
		this.producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

	}

	/**
	 * @see Sender#mail(String, String, String)
	 */
	@Override
	public void mail(String fromUser, String toUser, String content)
			throws JMSException {

		//Zum Senden einer Privatnachricht wird eine Queue erstellt
		Destination privateDestination = session.createQueue(toUser);
		
		//Mit dieser Queue wird ein neuer Message-Producer erstellt
		MessageProducer privateProducer = session
				.createProducer(privateDestination);
		privateProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		//Erstellen eines neuen Nachrichtenobjekts
		MessageData md = new ChatMessage(fromUser, content);

		//Senden des Nachrichtenobjekts
		ObjectMessage message = session.createObjectMessage(md);
		privateProducer.send(message);

	}

	/**
	 * @see Sender#broadcast(String, String)
	 */
	@Override
	public void broadcast(String fromUser, String content) throws JMSException {

		//Neues Nachrichtenobjekt erstellen
		MessageData md = new ChatMessage(fromUser, content);

		//Dieses Objekt an alle User im Chatraum senden
		ObjectMessage message = session.createObjectMessage(md);
		producer.send(message);

	}

	@Override
	public void stop() throws JMSException {
		this.producer.close();
		this.session.close();
		this.connection.close();
		
	}

}
