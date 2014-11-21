package dezsys06;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.log4j.chainsaw.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

public class TestChatConsoleReader {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private static ChatConsoleReader chat;
	
	
	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    String[] args = {};
	    Main.main(args);
	    Main.READER;
	}
	
	@Test
	public void test() {
		String[] args = {"Hallo", "hj"};
		chat.proccessCommand("test", args);
		assertEquals("hello\n", outContent.toString());
	}
	
	@After
	public void cleanUpStreams() {
	    System.setOut(null);
	}
}
