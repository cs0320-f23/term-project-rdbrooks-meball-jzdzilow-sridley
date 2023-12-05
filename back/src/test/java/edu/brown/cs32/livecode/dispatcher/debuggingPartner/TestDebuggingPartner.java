package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

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

  /** Tests the creation of a Debugging Partner and default state */
  @Test
  public void createDebuggingPartner() {
    DebuggingPartner testDebuggingPartner = new DebuggingPartner("Sarah");
    Assertions.assertEquals(testDebuggingPartner.getName(), "Sarah");
    Assertions.assertEquals(testDebuggingPartner.getCurrentHelpRequester(), null);
    Assertions.assertEquals(testDebuggingPartner.getFlagged(), false);
    Assertions.assertEquals(testDebuggingPartner.getStudentsHelped(), 0);
  }
}
