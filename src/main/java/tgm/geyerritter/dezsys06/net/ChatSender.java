package tgm.geyerritter.dezsys06.net;

import tgm.geyerritter.dezsys06.data.ChatMessage;
import tgm.geyerritter.dezsys06.data.MessageData;

import javax.jms.*;

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
	private Session privateSession;

	/**
	 * Initialisierung des Senders
	 * 
	 * @param connection bereits aufgebaute Connection zum Message-Broker
	 * @param chatroom Chatraum, in dem sich der User befindet
	 * @throws JMSException Fehler waehrend der Kommunikation
	 */
	public ChatSender(Connection connection, String chatroom) throws JMSException {

		// Session erstellen
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(chatroom);

		// Producer erstellen zum Senden der Nachrichten
		this.producer = session.createProducer(destination);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		
		/* Privatchat */
		
		//Private Session initialisieren
		this.privateSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

	}

	/**
	 * @see Sender#mail(String, String, String)
	 */
	@Override
	public void mail(String fromUser, String toUser, String content) throws JMSException {

		// Zum Senden einer Privatnachricht wird eine Queue erstellt
		Destination privateDestination = privateSession.createQueue(toUser);

		// Mit dieser Queue wird ein neuer Message-Producer erstellt
		MessageProducer privateProducer = privateSession.createProducer(privateDestination);
		privateProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		// Erstellen eines neuen Nachrichtenobjekts
		MessageData md = new ChatMessage(fromUser, content);

		// Senden des Nachrichtenobjekts
		ObjectMessage message = privateSession.createObjectMessage(md);
		privateProducer.send(message);

	}

	/**
	 * @see Sender#broadcast(String, String)
	 */
	@Override
	public void broadcast(String fromUser, String content) throws JMSException {

		// Neues Nachrichtenobjekt erstellen
		MessageData md = new ChatMessage(fromUser, content);

		// Dieses Objekt an alle User im Chatraum senden
		ObjectMessage message = session.createObjectMessage(md);
		producer.send(message);

	}

	/**
	 * @see Sender#stop()
	 */
	@Override
	public void stop() throws JMSException {
		this.producer.close();
		this.session.close();

	}

}
