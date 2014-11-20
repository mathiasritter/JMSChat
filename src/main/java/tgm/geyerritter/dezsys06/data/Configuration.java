package tgm.geyerritter.dezsys06.data;

/**
 * Dieses Interface beinhaelt die 
 * 
 * @author sgeyer, mritter
 * @version 1.0
 */
public interface Configuration {

	/**
	 * @return Der Username mit dem eine Verbindung zum Server erstellt werden soll
	 */
	public String getUser();
	
	/**
	 * @return Das Passwort fuer den User
	 */
	public String getPassword();
	
	/**
	 * @return Die Addresse unter der der Server erreichbar ist
	 */
	public String getHostAddress();
	
	/**
	 * @return Der Name fuer den ein Topic erstellt wird
	 */
	public String getSystemName();
}
