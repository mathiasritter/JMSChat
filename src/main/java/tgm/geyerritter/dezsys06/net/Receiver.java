package tgm.geyerritter.dezsys06.net;

import javax.jms.JMSException;

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
	 * @param username
	 *            Der Username des Users, dessen Postfach abgefragt werden soll.
	 *            
	 * @throws JMSException Wird geworfen wenn eineFehler bei der Kommunikation mit dem Server auftritt.
	 */
	public void getMails(String username) throws JMSException;

	/**
	 * Stoppt den Receiver, sodass dieser keine weiteren Nachrichten mehr
	 * erhaelt.
	 * 
	 * @throws JMSException Wird geworfen wenn eineFehler bei der Kommunikation mit dem Server auftritt.
	 */
	public void stop() throws JMSException;

}
