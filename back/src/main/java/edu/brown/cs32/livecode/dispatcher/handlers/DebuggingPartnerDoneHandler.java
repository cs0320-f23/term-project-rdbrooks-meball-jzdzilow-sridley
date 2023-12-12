package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the DebuggingPartnerDoneHandler class, implements the Route interface such that it
 * can be attached to the endpoint /debuggingPartnerDone.
 *
 * <p>A call to the /debuggingPartnerDone endpoint removes a DebuggingPartner from the queue.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class DebuggingPartnerDoneHandler implements Route {
  private final HelpRequesterQueue helpRequesterQueue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  /**
   * Constructor for the DebuggingPartnerDoneHandler class
   *
   * @param helpRequesterQueue DebuggingPartnerQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param sessionState SessionState representing the current state of the session
   */
  public DebuggingPartnerDoneHandler(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /debuggingPartnerDone endpoint
   *
   * @param request Request object containing parameters
   * @param response Response object that is unused
   * @return Json Object containing result of this request
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String name = request.queryParams("name");
    String email = request.queryParams("email");
    String record = request.queryParams("record");
    if (name == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: name")
          .serialize();
    } else if (email == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: email")
          .serialize();
    }

    boolean setSuccess = debuggingPartnerQueue.removeDebuggingPartner(name, email);
    if (record != null && record.equals("no")) {
      debuggingPartnerQueue.removeFromAttendanceList(name, email);
      helpRequesterQueue.rematchByDebuggingPartner(name, email);
    }
    if (setSuccess) {
      return new SuccessResponse("success", "Debugging Partner " + name + " has left!").serialize();
    } else {
      return new FailureResponse(
              "error_bad_request", "Debugging Partner " + name + " not found in queue.")
          .serialize();
    }
  }
}
