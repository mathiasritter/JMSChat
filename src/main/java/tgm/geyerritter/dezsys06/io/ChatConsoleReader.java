package tgm.geyerritter.dezsys06.io;

import java.util.Arrays;
import java.util.Scanner;

public class ChatConsoleReader implements ConsoleReader {

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
		if (commandLabel.equalsIgnoreCase("")) {
			
		}
	}

}
