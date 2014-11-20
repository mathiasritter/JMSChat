package tgm.geyerritter.dezsys06;

/**
 * Der Receiver empfaengt Nachrichten einer MoM.
 * 
 * @author mritter
 * @author sgeyer
 * @version 1.0
 */
public interface Receiver extends Runnable {
	
	/**
	 * Diese Methode fragt das private Postfach eines angegebenen Users ab.
	 * 
	 * @return Inhalt einer Nachricht
	 * @param username Der Username des Users, dessen Postfach abgefragt werden soll.
	 */
	public MessageData getMails(String username);

}
