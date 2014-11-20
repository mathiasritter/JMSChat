package tgm.geyerritter.dezsys06.io;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class ChatConsoleReader implements ConsoleReader {

	public static final Logger logger = LogManager.getLogger(ChatConsoleReader.class);
	
	public void run() {
		Scanner scanner = new Scanner(System.in);

		String line;
		while ((line = scanner.nextLine()) != null) {
			String label = "";
			String[] args = line.split(" ");
			if (args.length > 0) {
				label = args[0];
				args = Arrays.copyOfRange(args, 1, args.length);
			}
			
			proccessCommand(label, args);
		}
		
		scanner.close();
	}

	public void proccessCommand(String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("vsdbchat")) {
			if (args.length > 2) {
				String ip = args[0];
				String user = args[1];
				String chatroom = args[2];
				
				Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ip, chatroom);
				NetworkController net = new Networking(user, conf);
				
				logger.info("Connected.");
			} else {
				logger.info("Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>");
			}
		}
	}

}
