package dezsys06;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

public class TestChatConsoleReader {

	private ChatConsoleReader chat;


	private final String notConnected = "You need to connect to a server before you can chat";
	private TestAppender testAppender;

	@Before
	public void setUpStreams() throws InterruptedException {
		this.chat = new ChatConsoleReader();
		 testAppender = new TestAppender();
		 Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	public void test1() {
		String[] args = {};
		chat.proccessCommand("Bla", args);
		assertEquals(notConnected, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void test2() {
		String[] args = {};
		chat.proccessCommand("Bla", args);
		assertEquals(notConnected, testAppender.getLog().get(0).getMessage());
	}

}
