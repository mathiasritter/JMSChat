package tgm.geyerritter.dezsys06.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.jms.JMSException;

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

		logger.info("Please connect to message broker. For more information type 'help'");
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

	/**
	 * @see ConsoleReader#proccessCommand(String, String[])
	 */
	public void proccessCommand(String commandLabel, String[] args) {

		// Das Label ist immer das erste "Wort", der Rest sind die Argumente
		// Kommando "vsdbchat"
		if (commandLabel.equalsIgnoreCase("vsdbchat")) {
			if (args.length > 2) {
				String ip = args[0];
				String user = args[1];
				String chatroom = args[2];

				String noport_con = "Added default port (61616) to ip because no port was given. Connecting...";
				
				if (ip.lastIndexOf('/') > -1) {
					if (!ip.substring(ip.lastIndexOf('/'), ip.length()).contains(":")) {
						ip += ":61616";
						logger.info(noport_con);
					}
				} else {
					if (!ip.contains(":")) {
						ip += ":61616";
						logger.info(noport_con);
					}
				}

				// Protokolle angeben falls es der User nicht getan hat
				if (!ip.startsWith("failover://tcp://"))
					ip = "failover://tcp://" + ip;

				Configuration conf = new ExplicitConfiguration(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, ip, chatroom);
				try {
					// Mit den daten eine Verbindung aufbauen
					this.controller = new Networking(user, conf);
					logger.info("Connection established.");
				} catch (JMSException e) {
					logger.error("Error while connecting to Broker");
				}

			} else {
				logger.info("Not enough arguments. Correct usage: vsdbchat <ip_message_broker> <benutzername> <chatroom>");
			}
		// Kommando "mail"
		} else if (commandLabel.equalsIgnoreCase("mail")) {
			if (args.length > 1) {
				
				// Die nachricht aus den Argumenten filtern und zusammenbauen
				String msg = StringUtils.join(Arrays.asList(Arrays.copyOfRange(args, 1, args.length)), " ");

				// Mail senden
				this.controller.mail(args[0], msg);
				logger.info("Mail sent!");
			} else {
				logger.info("Not enough arguments. Correct usage: mail <benutzername> <nachricht>");
			}
		// Kommando "mailbox"
		} else if (commandLabel.equalsIgnoreCase("mailbox")) {
			// Printet alle fuer diesen Client vorgesehenen Mails in die Konsole
			this.controller.getMails();
		// Kommando "exit"
		} else if (commandLabel.equalsIgnoreCase("exit")) {
			logger.info("Closing connection...");
			if (this.controller != null)
				this.controller.halt();
			else
				System.exit(0);
		// Kommando "help"
		} else if (commandLabel.equalsIgnoreCase("help")) {
			// Kommandos fuer den derzeitigen stand der Verbindung ausgeben
			if (this.controller == null) {
				logger.info("Enter \"vsdbchat <ip_message_broker> <benutzername> <chatroom>\" to connect to a server");
			} else {
				logger.info("Enter \"mail <receiver> <message>\" to send a mail to someone");
				logger.info("Enter \"mailbox\" to fetch all your mails");
				logger.info("Enter \"exit\" to exit");
				logger.info("Just write anything to broadcast");
			}
		// Ansonsten wird die Nachricht als Text aufgefasst
		} else {
			if (this.controller != null) {
				List<String> text = new ArrayList<String>();
				text.add(commandLabel);
				text.addAll(Arrays.asList(args));
				this.controller.broadcast(StringUtils.join(text, " "));
			} else {
				logger.info("You need to connect to a server before you can chat");
			}
		}
	}

	public void setController(NetworkController controller) {
		this.controller = controller;
	}

}
