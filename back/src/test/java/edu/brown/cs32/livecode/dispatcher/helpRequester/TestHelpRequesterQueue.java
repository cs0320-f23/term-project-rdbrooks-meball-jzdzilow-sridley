package edu.brown.cs32.livecode.dispatcher.helpRequester;

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
    Assertions.assertEquals(helpRequesterQueue.getNeedHelp(), List.of());
    Assertions.assertEquals(helpRequesterQueue.getHelpedList(), List.of());
  }
}
