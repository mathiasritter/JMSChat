package dezsys06;

import static org.junit.Assert.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.data.StaticConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class NetworkingTest {

	private TestAppender testAppender;
	private final int timeout = 100;
	
	@Before
	public void setup() {
		this.testAppender = new TestAppender();
		Logger.getRootLogger().addAppender(testAppender);
	}

	@Test
	public void testNetworking() throws InterruptedException {
		NetworkController n = new Networking("test", new StaticConfiguration());
		n.broadcast("Ich bin eine Nachricht");
		Thread.sleep(timeout);
		assertTrue(((String) this.testAppender.getLog().get(0).getMessage()).contains("Ich bin eine Nachricht"));
	}

	@Test(expected = NullPointerException.class)
	public void testNetworking1() {
		NetworkController n = new Networking(null, new StaticConfiguration());
	}

	@Test(expected = NullPointerException.class)
	public void testNetworking2() {
		NetworkController n = new Networking("", null);
	}
	
	@Test
	public void testNetworking3() throws InterruptedException {
		NetworkController n = new Networking("Dondi", new StaticConfiguration());
		n.mail("Dondi", "Fibern's doch mit!");
		n.getMails();
		assertEquals(" Dondi: Fibern's doch mit!", ((String)this.testAppender.getLog().get(1).getMessage()).substring(((String)this.testAppender.getLog().get(1).getMessage()).indexOf(']') + 1, ((String)this.testAppender.getLog().get(1).getMessage()).length()));
	}
	
	@Test
	public void testNetworking4() {
		NetworkController n = new Networking("Dondi", new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ActiveMQConnection.DEFAULT_BROKER_URL, "Chat"));
		n.halt();
	}
}
