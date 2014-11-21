package dezsys06;

import static org.junit.Assert.*;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.data.StaticConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class NetworkingTest {

	private TestAppender testAppender;
	private final int timeout = 100;
	private final String closing = "Transport (tcp://127.0.0.1:61616) failed, reason:  java.io.EOFException, not attempting to automatically reconnect";
	
	 @Rule
	 public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@Before
	public void setup() {
		this.testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	public void testNetworking() throws InterruptedException, JMSException {
		NetworkController n = new Networking("test", new StaticConfiguration());
		n.broadcast("Ich bin eine Nachricht");
		Thread.sleep(timeout);
		assertTrue(((String) this.testAppender.getLog().get(0).getMessage()).contains("Ich bin eine Nachricht"));
	}

	@Test(expected = NullPointerException.class)
	public void testNetworking1() throws JMSException {
		NetworkController n = new Networking(null, new StaticConfiguration());
	}

	@Test(expected = NullPointerException.class)
	public void testNetworking2() throws JMSException {
		NetworkController n = new Networking("", null);
	}
	
	@Test
	public void testNetworking3() throws InterruptedException, JMSException {
		NetworkController n = new Networking("Dondi", new StaticConfiguration());
		n.mail("Dondi", "Fibern's doch mit!");
		Thread.sleep(timeout);
		n.getMails();
		assertEquals(" Dondi: Fibern's doch mit!", ((String)this.testAppender.getLog().get(1).getMessage()).substring(((String)this.testAppender.getLog().get(1).getMessage()).indexOf(']') + 1, ((String)this.testAppender.getLog().get(1).getMessage()).length()));
	}
	
	@Test
	public void testNetworking4() throws JMSException {
		NetworkController n = new Networking("Dondi", new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL, "Chat"));
		exit.expectSystemExit();
		exit.checkAssertionAfterwards(new Assertion() {
		      public void checkAssertion() {
		        assertEquals(closing, testAppender.getLog().get(0).getMessage());
		      }
		});
		n.halt();
	}
}
