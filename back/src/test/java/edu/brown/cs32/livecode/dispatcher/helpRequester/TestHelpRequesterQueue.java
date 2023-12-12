package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the HelpRequester class, which contains multiple lists of
 * HelpRequesters.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestHelpRequesterQueue {
  /** Testing creation of HelpRequesterQueue and default state */
  @Test
  public void createHelpRequesterQueue() {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(List.of());
    Assertions.assertEquals(helpRequesterQueue.getAllHelpRequesters(), List.of());
    Assertions.assertEquals(helpRequesterQueue.getGettingHelpList(), List.of());
    Assertions.assertEquals(helpRequesterQueue.getNeedHelp().hasNext(), false);
    Assertions.assertEquals(helpRequesterQueue.getNeedHelpList(), List.of());
    Assertions.assertEquals(helpRequesterQueue.getHelpedList(), List.of());
  }

  /** Testing update of HelpRequesterQueue */
  @Test
  public void updateHelpRequesterQueue() {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    HelpRequester sarah = new HelpRequester("Sarah", "sarah@gmail.com", "bug");
    DebuggingPartner claire = new DebuggingPartner("Claire", "claire@gmail.com");
    helpRequesterQueue.addNeedsHelp(sarah);
    sarah.setDebuggingPartner(claire);
    helpRequesterQueue.claimHelpRequester(sarah);
    helpRequesterQueue.setEscalated("Sarah", "sarah@gmail.com");
    Assertions.assertEquals(true, helpRequesterQueue.checkPaired("Claire", "Sarah"));
    helpRequesterQueue.moveBackToQueue("Sarah", "sarah@gmail.com");
    Assertions.assertEquals(List.of(sarah), helpRequesterQueue.getNeedHelpList());
    helpRequesterQueue.claimHelpRequester(sarah);
    Assertions.assertEquals(true, helpRequesterQueue.setDoneDebugging("Sarah", "sarah@gmail.com"));
    helpRequesterQueue.reset();
    Assertions.assertEquals(new ArrayList<>(), helpRequesterQueue.getAllHelpRequesters());
    helpRequesterQueue.claimHelpRequester(sarah);
    sarah.setDebuggingPartner(claire);
    helpRequesterQueue.rematchByDebuggingPartner("Claire", "claire@gmail.com");
    Assertions.assertEquals(false, helpRequesterQueue.checkPaired("Claire", "Sarah"));
    Assertions.assertEquals(true, helpRequesterQueue.setDoneDebugging("Sarah", "sarah@gmail.com"));
  }
}
