package tgm.geyerritter.dezsys06;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tgm.geyerritter.dezsys06.gui.ConnectWindow;
import tgm.geyerritter.dezsys06.gui.GUIAppender;
import tgm.geyerritter.dezsys06.gui.GUIPrinter;
import tgm.geyerritter.dezsys06.io.ChatConsoleReader;

import java.util.ArrayList;
import java.util.List;

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
		new Thread(ChatConsoleReader.getInstance()).start();

        // Start GUI
        ConnectWindow.launch(ConnectWindow.class);


    }

}
