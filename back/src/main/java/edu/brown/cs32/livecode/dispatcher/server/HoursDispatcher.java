package edu.brown.cs32.livecode.dispatcher.server;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

/**
 * Revised version of the prior dispatcher example from Tim's lecture. This dispatcher handles
 * matching DebuggingPartners with HelpRequesters.
 *
 * @author timnelson sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.1
 */
public class HoursDispatcher {
  private final HelpRequesterQueue helpRequesterQueue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private final String statusMessage;

  /**
   * Constructor for the HoursDispatcher class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param statusMessage String representing current status
   */
  public HoursDispatcher(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      String statusMessage) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.statusMessage = statusMessage;
  }

  /**
   * Getter for the statusMessage field
   *
   * @return String representing the statusMessage field
   */
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * Dispatches free DebuggingPartners to waiting HelpRequesters
   *
   * @param sessionState SessionState representing the current state of the session
   */
  public void dispatch(SessionState sessionState, boolean test) {
    System.out.println(
        Utils.timestamp()
            + " Dispatcher: Welcome to edu.brown.cs32.livecode.threads.TA hours! Today we're discussing "
            + this.getStatusMessage());
    // Only loop while the session is still running
    int iterations = 0;
    // If we're in testing mode, we do max 5 iterations
    while (sessionState.getRunning() && (!test || iterations < 2)) {
      iterations++;
      if (helpRequesterQueue.getNeedHelp().hasNext()) {
        // Who will help this student?
        DebuggingPartner helper = null;
        while (sessionState.getRunning()) {
          synchronized (debuggingPartnerQueue) {
            DebuggingPartner debuggingPartner = this.debuggingPartnerQueue.nextDebuggingPartner();
            if (debuggingPartner != null && debuggingPartner.isFree()) {
              helper = debuggingPartner;
              debuggingPartner.incrementStudentsHelper();
              break;
            }
          }
        }
        if (helper == null) continue; // no available TA

        HelpRequester nextHelpRequester = helpRequesterQueue.getNeedHelp().next();
        nextHelpRequester.setDebuggingPartner(helper);
        helpRequesterQueue.claimHelpRequester(nextHelpRequester);

        // Help the student
        System.out.println(
            Utils.timestamp()
                + " Dispatcher: Hi "
                + nextHelpRequester
                + "; you'll be seen by "
                + helper);
        try {
          helper.seeStudent(nextHelpRequester);
        } catch (Exception e) {
          System.out.println(
              "Unexpected behavior: edu.brown.cs32.livecode.threads.TA timed out while assigning student!");
        }
      } else {
        try {
          // nobody is waiting; go to sleep for a bit
          System.out.println(
              Utils.timestamp()
                  + " Dispatcher: Nobody waiting in queue, will check again in three seconds.");
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          System.out.println("Dispatcher terminated.");
        }
      }
    }
    System.out.println(Utils.timestamp() + " Dispatcher: The current session has ended.");
  }
}
