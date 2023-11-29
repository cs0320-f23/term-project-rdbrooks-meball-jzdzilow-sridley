package edu.brown.cs32.livecode.dispatcher.server;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

/**
 * Revised version of the prior dispatcher example. This focuses on concurrency, *NOT* on defensive
 * programming, and so I simplified out some of the proxy example code.
 */
public class HoursDispatcher {
  private final HelpRequesterQueue queue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private final String statusMessage;

  HoursDispatcher(
      HelpRequesterQueue signups,
      DebuggingPartnerQueue debuggingPartnerQueue,
      String statusMessage) {
    this.queue = signups;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.statusMessage = statusMessage;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void dispatch(SessionState sessionState) {
    System.out.println(
        Utils.timestamp()
            + " Dispatcher: Welcome to edu.brown.cs32.livecode.threads.TA hours! Today we're discussing "
            + this.getStatusMessage());
    //noinspection InfiniteLoopStatement
    while (sessionState.getRunning()) {
      if (queue.getNeedHelp().hasNext()) {
        // Who will help this student?
        DebuggingPartner helper = null;
        // while(true) {
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

        // Who to see next?
        //  (Question: why set this here, rather than before the edu.brown.cs32.livecode.threads.TA
        // loop?)
        HelpRequester nextHelpRequester = queue.getNeedHelp().next();
        nextHelpRequester.setDebuggingPartner(helper);
        queue.claimHelpRequester(nextHelpRequester);

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
          // TODO Return student to queue
          // ...
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
