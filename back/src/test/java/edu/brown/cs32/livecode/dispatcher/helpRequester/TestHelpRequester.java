package edu.brown.cs32.livecode.dispatcher.helpRequester;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the HelpRequester class, which represents a student that has
 * signed up as a help requester.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestHelpRequester {
  /** Testing creating a help requester with default state */
  @Test
  public void createHelpRequester() {
    HelpRequester testHelpRequester =
        new HelpRequester("Sarah", "sarah_ridley@brown.edu", "conceptual");
    Assertions.assertEquals(testHelpRequester.getName(), "Sarah");
    Assertions.assertEquals(testHelpRequester.getEmail(), "sarah_ridley@brown.edu");
    Assertions.assertEquals(testHelpRequester.getBugType(), "conceptual");
    Assertions.assertEquals(testHelpRequester.getDebugged(), false);
    Assertions.assertEquals(testHelpRequester.getEscalated(), false);
    Assertions.assertEquals(testHelpRequester.getDebuggingPartner(), null);
  }
}
