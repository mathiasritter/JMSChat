package tgm.geyerritter.dezsys06;

import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

/**
 * Main-Klasse mit Main-Methode zum Start des Chats
 * 
 * @author mritter
 * @version sgeyer
 */
public class Main {
	
	/**
	 * Main-Methode zum Start des Chats
	 * 
	 * @param args
	 *            Kommandozeilenargumente
	 */
	public static void main(String[] args) {
		
		
		// Chat in einem neuen Thread starten
		new Thread(new ChatConsoleReader()).start();

		
	}

}
