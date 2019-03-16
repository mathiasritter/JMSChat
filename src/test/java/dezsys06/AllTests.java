package dezsys06;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Ausfuehren aller Testfaelle
 *
 * 
 * @author mritter
 * @author sgeyer
 * @version 1.0
 */
@RunWith(Suite.class)
@SuiteClasses({ NetworkingTest.class, TestChatConsoleReader.class })
public class AllTests {

}
