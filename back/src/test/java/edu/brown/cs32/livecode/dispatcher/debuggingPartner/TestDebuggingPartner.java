package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the DebuggingPartner class, which represents a student that
 * has signed up as a debugging partner.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestDebuggingPartner {

  /** Tests the creation of a DebuggingPartner and default state */
  @Test
  public void createDebuggingPartner() {
    DebuggingPartner testDebuggingPartner = new DebuggingPartner("Sarah", "sarah_ridley@brown.edu");
    Assertions.assertEquals(testDebuggingPartner.getName(), "Sarah");
    Assertions.assertEquals(testDebuggingPartner.getEmail(), "sarah_ridley@brown.edu");
    Assertions.assertEquals(testDebuggingPartner.getCurrentHelpRequester(), null);
    Assertions.assertEquals(testDebuggingPartner.getFlagged(), false);
    Assertions.assertEquals(testDebuggingPartner.getStudentsHelped(), 0);
    Assertions.assertEquals(testDebuggingPartner.getJoinedTime().split(":").length, 3);
    Assertions.assertEquals(testDebuggingPartner.getPairedAtTime(), null);
  }

  /**
   * Tests the updating of a DebuggingPartner
   *
   * @throws Exception if student cannot be seen
   */
  @Test
  public void updateDebuggingPartner() throws Exception {
    DebuggingPartner cato = new DebuggingPartner("Cato", "meow@gmail.com");
    HelpRequester rosie = new HelpRequester("Rosie", "meow_meow@gmail.com", "meow");
    Assertions.assertEquals(true, cato.isFree());
    cato.incrementStudentsHelper();
    Assertions.assertEquals(1, cato.getStudentsHelped());
    cato.setFlagged();
    Assertions.assertEquals(true, cato.getFlagged());
    cato.seeStudent(rosie);
    Assertions.assertEquals(rosie, cato.getCurrentHelpRequester());
    Assertions.assertEquals(false, cato.isFree());
    rosie.setDebugged(true);
    Thread.sleep(500);
    Assertions.assertEquals(null, cato.getCurrentHelpRequester());
  }
}
