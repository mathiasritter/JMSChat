package dezsys06;

import static org.junit.Assert.assertEquals;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;

import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;
import tgm.geyerritter.dezsys06.net.Networking;

public class TestChatConsoleReader {

	private ChatConsoleReader chat;
	private TestAppender testAppender;

	private final String notConnected = "You need to connect to a server before you can chat";
	private final String notEnoughArguments = "Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>";
	private final String notEnoughArgumentsMail = "Not enough arguments. Correct usage: mail <benutzername> <nachricht>";
	private final String privateBegin = "*** Begin of Private Messages ***";
	private final String privateEnd = "*** End of Private Messages ***";
	private final String help = "Enter \"vsdbchat <ip_message_broker> <benutzername> <chatroom>\" to connect to a server";
	private final String help2 = "Enter \"mail <receiver> <message>\" to send a mail to someone";
	private final String initialized = "Chat initialized. For more information type 'help'";
	private final String closing = "Closing connection...";
	private final String[] Connection = {"127.0.0.1:61616", "testuser", "testchat"};
	private final String[] Connection2 = {"failover://tcp://127.0.0.1:61616", "testuser", "testchat"};
	private final String[] emptyArgs = {};
	private final int timeout = 100;

	
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void init() {
		this.chat = new ChatConsoleReader();
		testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	public void testNotConnected1() {
		chat.proccessCommand("Bla", emptyArgs);
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
		chat.proccessCommand("Mail", emptyArgs);
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
		chat.proccessCommand("Mailbox", emptyArgs);
		assertEquals(privateBegin, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testMail3() {
		chat.proccessCommand("vsdbchat", Connection);
		chat.proccessCommand("Mailbox", emptyArgs);
		assertEquals(privateEnd, testAppender.getLog().get(1).getMessage());
	}
	
	@Test
	public void testHelp1() {
		chat.proccessCommand("help", emptyArgs);
		assertEquals(help, testAppender.getLog().get(0).getMessage());
	}
	
	
	@Test
	public void testHelp2() throws JMSException {
		Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "failover://tcp://127.0.0.1:61616", "testchat");
		chat.setController(new Networking("testuser", conf));
		chat.proccessCommand("help", emptyArgs);
		assertEquals(help2, testAppender.getLog().get(0).getMessage());
	}
	
	@Test
	public void testBroadcast1() throws JMSException, InterruptedException {
		Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "failover://tcp://127.0.0.1:61616", "testchat");
		chat.setController(new Networking("testuser", conf));
		chat.proccessCommand("Message1", emptyArgs);
		Thread.sleep(timeout);
		String receive = (String) testAppender.getLog().get(0).getMessage();
		receive = receive.substring(receive.lastIndexOf(' ')+1, receive.lastIndexOf('1'));
		assertEquals("Message", receive);
	}
	
	@Test
	public void testExit() throws JMSException, InterruptedException {
		Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "failover://tcp://127.0.0.1:61616", "testchat");
		chat.setController(new Networking("testuser", conf));
		exit.expectSystemExit();
		exit.checkAssertionAfterwards(new Assertion() {
		      public void checkAssertion() {
		        assertEquals(closing, testAppender.getLog().get(0).getMessage());
		      }
		});
		chat.proccessCommand("exit", emptyArgs);
	}
	

}
