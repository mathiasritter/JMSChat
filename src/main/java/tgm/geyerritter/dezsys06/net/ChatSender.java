package tgm.geyerritter.dezsys06.net;

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

	/**
	 * Initialisieren der Attribute im Konstruktor
	 * 
	 * @param session Aufgebaute Session zum Broker
	 * @param producer Producer fuer ein Topic
	 */
	public ChatSender(Session session, MessageProducer producer) {

		this.session = session;
		this.producer = producer;

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
		privateProducer.setDeliveryMode(DeliveryMode.PERSISTENT);

		//Erstellen eines neuen Nachrichtenobjekts
		MessageData md = new ChatMessage(fromUser, content);

		//Senden des Nachrichtenobjekts
		ObjectMessage message = session.createObjectMessage(md);
		producer.send(message);

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

}
