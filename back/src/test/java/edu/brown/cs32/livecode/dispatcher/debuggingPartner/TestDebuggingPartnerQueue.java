package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

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
  }
}
