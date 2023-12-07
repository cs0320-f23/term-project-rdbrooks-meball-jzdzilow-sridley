package edu.brown.cs32.livecode.dispatcher.server;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the HoursDispatch class, which manages pairing HelpRequesters
 * with DebuggingPartners while a session is running.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestHoursDispatcher {

  /** This tests creating an HoursDispatcher and testing default state */
  @Test
  public void createHoursDispatcher() {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    HoursDispatcher hoursDispatcher =
        new HoursDispatcher(helpRequesterQueue, debuggingPartnerQueue, "status");
    Assertions.assertEquals("status", hoursDispatcher.getStatusMessage());
  }

  /** This tests creating an HoursDispatcher and testing once it starts */
  @Test
  public void createHoursDispatcherStartedNoStudents() {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    SessionState sessionState = new SessionState(false);
    HoursDispatcher hoursDispatcher =
        new HoursDispatcher(helpRequesterQueue, debuggingPartnerQueue, "status");
    hoursDispatcher.dispatch(sessionState, true);
    Assertions.assertEquals("status", hoursDispatcher.getStatusMessage());
  }

  /** This tests creating an HoursDispatcher and testing once it starts and students are added */
  @Test
  public void createHoursDispatcherStartedWithStudents() throws InterruptedException {
    HelpRequester sarah = new HelpRequester("Sarah", "sarah_ridley@brown.edu", "bug");
    DebuggingPartner claire = new DebuggingPartner("Claire", "claire_ridley@brown.edu");
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    SessionState sessionState = new SessionState(false);
    sessionState.setRunning(true);
    helpRequesterQueue.addNeedsHelp(sarah);
    debuggingPartnerQueue.addDebuggingPartner(claire);
    HoursDispatcher hoursDispatcher =
        new HoursDispatcher(helpRequesterQueue, debuggingPartnerQueue, "status");
    hoursDispatcher.dispatch(sessionState, true);
    sessionState.setRunning(false);
    Assertions.assertEquals("status", hoursDispatcher.getStatusMessage());
    Thread.sleep(500);
    Assertions.assertEquals(claire, sarah.getDebuggingPartner());
  }
}
