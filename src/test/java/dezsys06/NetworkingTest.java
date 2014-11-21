package dezsys06;

import org.junit.Test;

import tgm.geyerritter.dezsys06.data.StaticConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class NetworkingTest {

	@Test
	public void testNetworking() {
		NetworkController n = new Networking("test", new StaticConfiguration());
		n.broadcast("Ich bin eine Nachricht");
	}
	
	@Test(expected = NullPointerException.class)
	public void testNetworking1() {
		NetworkController n = new Networking(null, new StaticConfiguration());
	}

	
}
