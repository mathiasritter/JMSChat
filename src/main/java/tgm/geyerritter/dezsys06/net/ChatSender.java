package tgm.geyerritter.dezsys06.net;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import tgm.geyerritter.dezsys06.data.ChatMessage;
import tgm.geyerritter.dezsys06.data.MessageData;

public class ChatSender implements Sender {

	private MessageProducer producer;
	private Session session;

	public ChatSender(Session session, MessageProducer producer) {

		this.session = session;
		this.producer = producer;

	}

	@Override
	public void mail(String fromUser, String toUser, String content)
			throws JMSException {

		Destination privateDestination = session.createQueue(toUser);
		MessageProducer privateProducer = session
				.createProducer(privateDestination);
		privateProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		MessageData md = new ChatMessage(fromUser, content);

		ObjectMessage message = session.createObjectMessage(md);
		producer.send(message);

	}

	@Override
	public void broadcast(String fromUser, String content) throws JMSException {

		MessageData md = new ChatMessage(fromUser, content);

		ObjectMessage message = session.createObjectMessage(md);
		producer.send(message);

	}

}
