package tgm.geyerritter.dezsys06.net;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tgm.geyerritter.dezsys06.data.MessageData;

public class ChatReceiver implements Receiver {

	private MessageConsumer consumer;
	private boolean run;
	private Session session;
	private static final Logger logger = LogManager
			.getLogger(ChatReceiver.class);

	public ChatReceiver(Session session, MessageConsumer consumer) {
		this.session = session;
		this.consumer = consumer;
		this.run = true;

	}

	@Override
	public void run() {
		while (run) {
			try {

				ObjectMessage message = (ObjectMessage) consumer.receive();

				if (message != null) {

					MessageData md = (MessageData) message.getObject();
					logger.info("[" + md.getCreationDate() + "] "
							+ md.getSender() + ": " + md.getContent());

					message.acknowledge();
				}
			} catch (JMSException e) {

				e.printStackTrace();
			}
		}

	}

	@Override
	public void getMails(String username) throws JMSException {
		Destination privateDestination = session.createQueue(username);
		MessageConsumer privateConsumer = session
				.createConsumer(privateDestination);

		logger.info("Begin of Private Messages");

		ObjectMessage message = (ObjectMessage) privateConsumer.receiveNoWait();

		while (message != null) {

			MessageData md = (MessageData) message.getObject();

			logger.info("[" + md.getCreationDate() + "] " + md.getSender()
					+ ": " + md.getContent());

			message.acknowledge();

			message = (ObjectMessage) privateConsumer.receiveNoWait();
		}

		logger.info("End of Private Messages");
	}

	@Override
	public void stop() {
		this.run = false;
	}

}
