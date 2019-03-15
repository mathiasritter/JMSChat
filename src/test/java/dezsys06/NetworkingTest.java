package dezsys06;

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

import javax.jms.JMSException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NetworkingTest {

	private TestAppender testAppender;
	private final int timeout = 100;

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Before
	/**
	 * Appender fuer Log4J initialisieren um den output analysieren zu koennen.
	 */
	public void setup() {
		this.testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	/**
	 * Testen ob eine Nachricht wieder ankommt
	 * 
	 * @throws InterruptedException
	 * @throws JMSException
	 */
	public void testNetworking() throws InterruptedException, JMSException {
		NetworkController n = new Networking("test", new StaticConfiguration());
		n.broadcast("Ich bin eine Nachricht");
		Thread.sleep(timeout);
		assertTrue(((String) this.testAppender.getLog().get(0).getMessage()).contains("Ich bin eine Nachricht"));
	}
	
	/**
	 * Testen was passiert wenn man Networking ohne username initalisiert
	 * 
	 * @throws JMSException
	 */
	@Test(expected = NullPointerException.class)
	public void testNetworking1() throws JMSException {
		NetworkController n = new Networking(null, new StaticConfiguration());
	}

	/**
	 * Testen was passiert wenn man Networking ohne config initalisiert
	 * 
	 * @throws JMSException
	 */
	@Test(expected = NullPointerException.class)
	public void testNetworking2() throws JMSException {
		NetworkController n = new Networking("", null);
	}

	@Test
	/**
	 * Testen ob eine mail wieder ankommt
	 * 
	 * @throws InterruptedException
	 * @throws JMSException
	 */
	public void testNetworking3() throws InterruptedException, JMSException {
		NetworkController n = new Networking("Dondi", new StaticConfiguration());
		n.mail("Dondi", "Fibern's doch mit!");
		Thread.sleep(timeout);
		n.getMails();
		assertEquals(" Dondi: Fibern's doch mit!", ((String) this.testAppender.getLog().get(1).getMessage()).substring(((String) this.testAppender.getLog().get(1).getMessage()).indexOf(']') + 1, ((String) this.testAppender.getLog().get(1).getMessage()).length()));
	}

	/**
	 * Test ob sich der {@link NetworkController} anhalten laesst 
	 */
	@Test
	public void testNetworking4() throws JMSException {
		NetworkController n = new Networking("Dondi", new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL, "Chat"));
		exit.expectSystemExit();
		exit.checkAssertionAfterwards(new Assertion() {
			public void checkAssertion() {
				// assertEquals(closing, testAppender.getLog().get(0).getMessage());
			}
		});
		n.halt();
	}
}
