package tgm.geyerritter.dezsys06.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Alle noetigen Daten die beim senden einer Nachricht enthalten sein sollten
 * werden in ein Objekt gewrappt.
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public interface MessageData extends Serializable {

	/**
	 * @return Der Username des senders
	 */
	public String getSender();
	
	/**
	 * @return Das Datum an dem die Nachricht erstellt wurde
	 */
	public Date getCreationDate();
	
	/**
	 * @return Der Inhalt der Nachricht
	 */
	public String getContent();
	
}
