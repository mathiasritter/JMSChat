package dezsys06;

import de.saxsys.javafx.test.JfxRunner;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.runner.RunWith;
import tgm.geyerritter.dezsys06.Broker;
import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;
import tgm.geyerritter.dezsys06.net.Networking;

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;

/**
 * Testen der Klasse {@link ChatConsoleReader}, welche die Eingabe des Users ueberprueft.
 * 
 * @author mritter
 * @author sgeyer
 * @version 1.0
 */
@RunWith(JfxRunner.class)
public class TestChatConsoleReader {

	private ChatConsoleReader chat;
	private TestAppender testAppender;

	private final String notConnected = "You need to connect to a server before you can chat";
	private final String notEnoughArguments = "Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>";
	private final String privateBegin = "*** Begin of Private Messages ***";
	private final String privateEnd = "*** End of Private Messages ***";
	private final String help = "Enter \"vsdbchat <ip_message_broker> <benutzername> <chatroom>\" to connect to a server";
	private final String help2 = "Enter \"mail <receiver> <message>\" to send a mail to someone";
	private final String initialized = "Please connect to message broker. For more information type 'help'";
	private final String closing = "Closing connection...";
	private final String defaultPort = "Added default port (61616) to ip because no port was given. Connecting...";
	private final String[] Connection = {"127.0.0.1:61616", "testuser", "testchat"};
	private final String[] Connection2 = {"failover://tcp://127.0.0.1:61616", "testuser", "testchat"};
	private final String[] emptyArgs = {};
	private final int timeout = 100;

    @BeforeClass
    public static void setupBroker() throws Exception {
        Broker.main(new String[0]);
    }

    @AfterClass
    public static void stopBroker() throws Exception {
        Broker.stopBroker();
    }
	
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	/**
	 * Initialisieren des ChatConsoleReaders und des Appenders
	 */
	@Before
	public void init() throws Exception {
		this.chat = ChatConsoleReader.getInstance();
		testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}
	
	/**
	 * Testen, ob eine falsche Eingabe zum Verbinden korrekt verarbeitet wird
	 */
	@Test
	public void testWrongConnection1() {
		String[] wrongConnection = {"Hallo"};
		chat.proccessCommand("vsdbchat", wrongConnection);
		assertEquals(notEnoughArguments, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob eine falsche Eingabe zum Verbinden korrekt verarbeitet wird
	 */
	@Test
	public void testWrongConnection2() {
		String[] wrongConnection = {"Hallo", "Freunde"};
		chat.proccessCommand("vsdbchat", wrongConnection);
		assertEquals(notEnoughArguments, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob eine korrekte Eingabe zu einem Verbindungsaufbau fuehrt
	 */
	@Test
	public void testRightConnection1() {
		chat.proccessCommand("vsdbchat", Connection);
	}

	/**
	 * Testen, ob eine korrekte Eingabe zu einem Verbindungsaufbau fuehrt
	 */
	@Test
	public void testRightConnection2() {
		chat.proccessCommand("vsdbchat", Connection2);
	}
	
	/**
	 * Testen, ob nach dem Starten des Programmes die korrekte Aufforderung zum Verbinden ausgegeben wird
	 */
	@Test
	public void testInitialized1() {
		new Thread(chat).start();
		chat.proccessCommand("vsdbchat", Connection);
		assertEquals(initialized, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob nach dem Starten des Programmes eine Verbindung mit Angabe des Ports aufgebaut werden kann
	 */
	@Test
	public void testInitialized3() {
		new Thread(chat).start();
		String[] args = { "127.0.0.1:61616", "testuser", "testchat" };
		chat.proccessCommand("vsdbchat", args);
		assertEquals(initialized, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob eine mail mit korrekter Eingabe gesendet werden kann
	 */
	@Test
	public void testMail1() {
		chat.proccessCommand("vsdbchat", Connection);
		String[] args = {"testuser", "bla"};
		chat.proccessCommand("Mail", args);
	}
	
	/**
	 * Testen, ob die Mailbox abgerufen werden kann
	 */
	@Test
	public void testMail2() {
		chat.proccessCommand("vsdbchat", Connection);
		chat.proccessCommand("Mailbox", emptyArgs);
		assertEquals(privateBegin, testAppender.getLog().get(1).getMessage());
	}
	
	/**
	 * Testen, ob die Mailbox abgerufen werden kann
	 */
	@Test
	public void testMail3() {
		chat.proccessCommand("vsdbchat", Connection);
		chat.proccessCommand("Mailbox", emptyArgs);
		assertEquals(privateEnd, testAppender.getLog().get(2).getMessage());
	}
	
	/**
	 * Testen, ob nach Eingabe von "Help" eine Hilfe angezeigt wird
	 */
	@Test
	public void testHelp1() {
		chat.proccessCommand("help", emptyArgs);
		assertEquals(help, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob nach Eingabe von "Help" nach dem verbindungsaufbau eine Hilfe angezeigt wird
	 */
	@Test
	public void testHelp2() throws JMSException {
		Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "failover://tcp://127.0.0.1:61616", "testchat");
		chat.setController(new Networking("testuser", conf));
		chat.proccessCommand("help", emptyArgs);
		assertEquals(help2, testAppender.getLog().get(0).getMessage());
	}
	
	/**
	 * Testen, ob eine Nachricht an alle korrekt gesendet wird
	 */
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
	
	/**
	 * Testen, ob nach Eingabe von "exit" die Verbindung geschlossen wird
	 */
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
