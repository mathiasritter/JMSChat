package tgm.geyerritter.dezsys06.data;

import java.util.Calendar;
import java.util.Date;

public class ChatMessage implements MessageData {
	private static final long serialVersionUID = 8985812390026821173L;
	
	private String sender;
	private String content;
	
	private Date creationDate;
	
	public ChatMessage(String sender, String content) {
		this.sender = sender;
		this.content = content;
		this.creationDate = Calendar.getInstance().getTime();
	}
	
	public String getSender() {
		return this.sender;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public String getContent() {
		return this.content;
	}
	
}
