package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
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
    Assertions.assertEquals(testHelpRequester.getJoinedTime().split(":").length, 3);
    Assertions.assertEquals(testHelpRequester.getPairedAtTime(), null);
  }

  /** Testing making updates to a help requester */
  @Test
  public void updateHelpRequester() {
    HelpRequester sarah = new HelpRequester("Sarah", "sarah_ridley@brown.edu", "bug");
    DebuggingPartner rachel = new DebuggingPartner("Rachel", "rachel_brooks@brown.edu");
    sarah.setDebuggingPartner(rachel);
    Assertions.assertEquals(rachel, sarah.getDebuggingPartner());
    sarah.setEscalated();
    Assertions.assertEquals(true, sarah.getEscalated());
    sarah.setDebugged(true);
    Assertions.assertEquals(true, sarah.getDebugged());
  }
}
