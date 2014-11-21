package dezsys06;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tgm.geyerritter.dezsys06.data.StaticConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class NetworkingTest {

	private ByteArrayOutputStream outContent;
	
	@Before
	public void setUpStreams() {
		outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
	}
	
	@Test
	public void testNetworking() {
		NetworkController n = new Networking("test", new StaticConfiguration());
		n.broadcast("Ich bin eine Nachricht");
		assertEquals("Ich bin eine Nachricht", "");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNetworking1() {
		NetworkController n = new Networking(null, new StaticConfiguration());
	}
	
	@Test(expected = NullPointerException.class)
	public void testNetworking2() {
		NetworkController n = new Networking("", null);
	}
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	}
}
