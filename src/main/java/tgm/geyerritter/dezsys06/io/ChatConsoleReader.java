package tgm.geyerritter.dezsys06.io;

import java.util.Arrays;
import java.util.Scanner;

import org.apache.activemq.ActiveMQConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tgm.geyerritter.dezsys06.data.Configuration;
import tgm.geyerritter.dezsys06.data.ExplicitConfiguration;
import tgm.geyerritter.dezsys06.net.NetworkController;
import tgm.geyerritter.dezsys06.net.Networking;

public class ChatConsoleReader implements ConsoleReader {

	public static final Logger logger = LogManager.getLogger(ChatConsoleReader.class);

	private NetworkController controller;

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
				
				if (!ip.startsWith("failover://tcp://"))
					ip = "failover://tcp://" + ip;

				Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ip, chatroom);
				this.controller = new Networking(user, conf);

				logger.info("Connected.");
			} else {
				logger.info("Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>");
			}
		} else if (commandLabel.equalsIgnoreCase("mail")) {
			if (args.length > 1) {
				String msg = "";

				for (int i = 1; i < args.length; i++)
					msg += args[i] + ", ";

				msg = msg.substring(0, msg.lastIndexOf(','));

				this.controller.mail(args[0], msg);
			} else {	
				logger.info("Not enough arguments. Correct usage: mail <benutzername> <nachricht>");
			}
		} else if (commandLabel.equalsIgnoreCase("mailbox")) {
			this.controller.getMails();
		} else if (commandLabel.equalsIgnoreCase("exit")) {
			logger.info("Closing connection...");
			this.controller.halt();
		} else {
			this.controller.broadcast(StringUtils.join(commandLabel, args));
		}
	}

}
