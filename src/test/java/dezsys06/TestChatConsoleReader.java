package dezsys06;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

public class TestChatConsoleReader {

	private ChatConsoleReader chat;
	private TestAppender testAppender;

	private final String notConnected = "You need to connect to a server before you can chat";
	private final String notEnoughArguments = "Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>";
	private final String notEnoughArgumentsMail = "Not enough arguments. Correct usage: mail <benutzername> <nachricht>";
	private final String privateBegin = "*** Begin of Private Messages ***";
	private final String privateEnd = "*** End of Private Messages ***";
	private final String exit = "Closing connection...";
	private final String help = "Enter \"vsdbchat <ip_message_broker> <benutzername> <chatroom>\" to connect to a server";
	private final String initialized = "Chat initialized. For more information type 'help'";
	private final String[] Connection = {"127.0.0.1:61616", "testuser", "testchat"};
	private final String[] Connection2 = {"failover://tcp://127.0.0.1:61616", "testuser", "testchat"};
	
	@Rule
	public final TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();
	
	
	@Before
	public void init() {
		this.chat = new ChatConsoleReader();
		testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	public void testNotConnected1() {
		String[] args = {};
		chat.proccessCommand("Bla", args);
		assertEquals(notConnected, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testNotConnected2() {
		String[] args = {"Hallo", "Freunde"};
		chat.proccessCommand("Bla", args);
		assertEquals(notConnected, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testWrongConnection1() {
		String[] wrongConnection = {"Hallo"};
		chat.proccessCommand("vsdbchat", wrongConnection);
		assertEquals(notEnoughArguments, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testWrongConnection2() {
		String[] wrongConnection = {"Hallo", "Freunde"};
		chat.proccessCommand("vsdbchat", wrongConnection);
		assertEquals(notEnoughArguments, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testRightConnection1() {
		chat.proccessCommand("vsdbchat", Connection);
	}

	@Test
	public void testRightConnection2() {
		chat.proccessCommand("vsdbchat", Connection2);
	}
	
	@Test
	public void testInitialized1() {
		new Thread(chat).start();
		chat.proccessCommand("vsdbchat", Connection);
		assertEquals(initialized, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testWrongMail1() {
		chat.proccessCommand("vsdbchat", Connection);
		String[] args = {};
		chat.proccessCommand("Mail", args);
		assertEquals(notEnoughArgumentsMail, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testMail1() {
		chat.proccessCommand("vsdbchat", Connection);
		String[] args = {"testuser", "bla"};
		chat.proccessCommand("Mail", args);
	}
	
	@Test
	public void testMail2() {
		chat.proccessCommand("vsdbchat", Connection);
		String[] args = {};
		chat.proccessCommand("Mailbox", args);
		assertEquals(privateBegin, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testMail3() {
		chat.proccessCommand("vsdbchat", Connection);
		String[] args = {};
		chat.proccessCommand("Mailbox", args);
		assertEquals(privateEnd, testAppender.getLog().get(1).getMessage());
	}
	
	@Test
	public void testHelp1() {
		String[] args = {};
		chat.proccessCommand("help", args);
		assertEquals(help, testAppender.getLog().get(0).getMessage());
	}
	
	
	@Test
	public void testHelp2() {
		new Thread(chat).start();
		systemInMock.provideText("vsdbchat 127.0.0.1:61616 testuser testchat\n");
		assertEquals(initialized, testAppender.getLog().get(0).getMessage());
	}
	

}
