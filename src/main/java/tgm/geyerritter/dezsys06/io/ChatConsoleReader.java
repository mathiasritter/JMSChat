package tgm.geyerritter.dezsys06.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.activemq.ActiveMQConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

/**
 * Diese Klasse verarbeitet die Eingaben des Users in der Konsole
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public class ChatConsoleReader implements ConsoleReader {

	public static final Logger logger = LogManager.getLogger(ChatConsoleReader.class);

	private NetworkController controller;

	public void run() {

		logger.info("Chat initialized. For more information type 'help'");
		Scanner scanner = new Scanner(System.in);

		// Warte auf Benutzeingaben
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
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

		// Das Label ist immer das erste "Wort", der Rest sind die Argumente
		if (commandLabel.equalsIgnoreCase("vsdbchat")) {
			if (args.length > 2) {
				String ip = args[0];
				String user = args[1];
				String chatroom = args[2];

				// Protokolle angeben falls es der User nicht getan hat
				if (!ip.startsWith("failover://tcp://"))
					ip = "failover://tcp://" + ip;

				Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ip, chatroom);
				this.controller = new Networking(user, conf);

			} else {
				logger.info("Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>");
			}
		} else if (commandLabel.equalsIgnoreCase("mail")) {
			if (args.length > 1) {
				String msg = "";

				for (int i = 1; i < args.length; i++)
					msg += args[i] + " ";

				this.controller.mail(args[0], msg);
			} else {
				logger.info("Not enough arguments. Correct usage: mail <benutzername> <nachricht>");
			}
		} else if (commandLabel.equalsIgnoreCase("mailbox")) {
			this.controller.getMails();
		} else if (commandLabel.equalsIgnoreCase("exit")) {
			logger.info("Closing connection...");
			this.controller.halt();
		} else if (commandLabel.equalsIgnoreCase("help")) {
			if (this.controller == null) {
				logger.info("Enter \"vsdbchat <ip_message_broker> <benutzername> <chatroom>\" to connect to a server");
			} else {
				logger.info("Enter \"mail <receiver> <message>\" to send a mail to someone");
				logger.info("Enter \"mailbox\" to fetch all your mails");
				logger.info("Just write anything to broadcast");
			}
		} else {
			if (this.controller != null) {
				List<String> text = new ArrayList<String>();
				text.addAll(Arrays.asList(args));
				text.add(commandLabel);
				this.controller.broadcast(StringUtils.join(text, " "));
			} else {
				logger.info("You need to connect to a server before you can chat");
			}
		}
	}

}
