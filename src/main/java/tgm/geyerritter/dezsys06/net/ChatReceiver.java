package tgm.geyerritter.dezsys06.net;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;

import tgm.geyerritter.dezsys06.data.MessageData;

public class ChatReceiver implements Receiver {

	private MessageProducer producer;
	private Destination destination;
	private Session session;
	
	public ChatReceiver() {
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MessageData getMails(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
