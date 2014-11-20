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
	 */
	public void getMails(String username) throws JMSException;

	/**
	 * Stoppt den Receiver, sodass dieser keine weiteren Nachrichten mehr
	 * erhaelt.
	 */
	public void stop() throws JMSException;

}
