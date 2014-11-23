package tgm.geyerritter.dezsys06.io;

/**
 * Hier steht die Verarbeitung des Inputs des Users im Vordergrund.
 * Je nach Eingabe werden andere Kommados aufgefuehrt
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public interface ConsoleReader extends Runnable {
	
	/**
	 * Diese Methode verarbeitet ein Kommando das vom User eingegeben wurde
	 * 
	 * @param comandLabel Der Name des Kommandos
	 * @param args Argumente falls vom User angegeben
	 */
	public void proccessCommand(String comandLabel, String[] args);
	
}
