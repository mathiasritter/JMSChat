package tgm.geyerritter.dezsys06.net;

import javax.jms.JMSException;

/**
 * Der Sender sendet Nachrichten an eine MoM
 * 
 * @author mritter
 * @author sgeyer
 * @version 1.0
 */
public interface Sender {

	/**
	 * Senden einer Nachricht an alle User.<br>
	 * Der Username des Absenders und die Nachricht muessen als String angegeben
	 * werden.
	 * 
	 * @param content
	 *            Inhalt einer zu sendenen Nachricht
	 * @param fromUser
	 *            Username des Absenders
	 */
	public void broadcast(String fromUser, String content) throws JMSException;

	/**
	 * Senden einer Privatnachricht an einen bestimmten User.<br>
	 * Der Username des Absenders, des Empfaengers und die Nachricht muessen als
	 * String angegeben werden.
	 * 
	 * @param fromUser
	 *            Sender-Username
	 * @param toUser
	 *            Empfaenger-Username
	 * @param content
	 *            Inhalt einer zu sendenen Privat-Nachricht
	 */
	public void mail(String fromUser, String toUser, String content)
			throws JMSException;

}
