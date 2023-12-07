package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the DebuggingPartner class, which represents a student that
 * has signed up as a debugging partner.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestDebuggingPartnerQueue {

  /** Tests the creation of a DebuggingPartnerQueue and default state */
  @Test
  public void createDebuggingPartnerQueue() {
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(List.of());
    Assertions.assertEquals(debuggingPartnerQueue.getDebuggingPartnerList(), List.of());
    Assertions.assertEquals(debuggingPartnerQueue.getAllDebuggingPartnerList(), List.of());
    Assertions.assertEquals(debuggingPartnerQueue.nextDebuggingPartner(), null);
  }

  /** Tests the updating of a DebuggingPartnerQueue */
  @Test
  public void updateDebuggingPartnerQueue() {
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    DebuggingPartner claire = new DebuggingPartner("Claire", "claire@gmail.com");
    DebuggingPartner sarah = new DebuggingPartner("Sarah", "sarah@gmail.com");
    debuggingPartnerQueue.addDebuggingPartner(claire);
    Assertions.assertEquals(claire, debuggingPartnerQueue.nextDebuggingPartner());
    // Again to test looping index
    Assertions.assertEquals(claire, debuggingPartnerQueue.nextDebuggingPartner());
    debuggingPartnerQueue.addDebuggingPartner(sarah);
    Assertions.assertEquals(
        true, debuggingPartnerQueue.removeDebuggingPartner("Claire", "claire@gmail.com"));
    Assertions.assertEquals(
        true, debuggingPartnerQueue.removeFromAttendanceList("Claire", "claire@gmail.com"));
    Assertions.assertEquals(List.of(sarah), debuggingPartnerQueue.getAllDebuggingPartnerList());
    debuggingPartnerQueue.addDebuggingPartner(claire);
    Assertions.assertEquals(
        true, debuggingPartnerQueue.removeAndFlagDebuggingPartner("Claire", "claire@gmail.com"));
    Assertions.assertEquals(true, claire.getFlagged());
    debuggingPartnerQueue.reset();
    Assertions.assertEquals(
        false, debuggingPartnerQueue.removeDebuggingPartner("Claire", "claire@gmail.com"));
    debuggingPartnerQueue.removeFromAttendanceList("Claire", "claire@gmail.com");
    Assertions.assertEquals(List.of(), debuggingPartnerQueue.getAllDebuggingPartnerList());
  }
}
