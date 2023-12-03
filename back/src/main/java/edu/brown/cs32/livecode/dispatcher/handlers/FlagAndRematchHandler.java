package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

public class FlagAndRematchHandler implements Route {

  private DebuggingPartnerQueue debuggingPartnerQueue;
  private HelpRequesterQueue helpRequesterQueue;
  private SessionState sessionState;

  public FlagAndRematchHandler(HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.helpRequesterQueue = helpRequesterQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String debuggingPartnerName = request.queryParams("debuggingPartnerName");
    String debuggingPartnerEmail = request.queryParams("debuggingPartnerEmail");
    String helpRequesterName = request.queryParams("helpRequesterName");
    String helpRequesterEmail = request.queryParams("helpRequesterEmail");
    if (debuggingPartnerName == null) {
      return new FailureResponse(
          "error_bad_request", "Missing some required parameter: debuggingPartnerName")
          .serialize();
    } else if (debuggingPartnerEmail == null) {
      return new FailureResponse(
          "error_bad_request", "Missing some required parameter: debuggingPartnerEmail")
          .serialize();
    } else if (helpRequesterName == null) {
      return new FailureResponse(
          "error_bad_request", "Missing some required parameter: helpRequesterName")
          .serialize();
    } else if (helpRequesterEmail == null) {
      return new FailureResponse(
          "error_bad_request", "Missing some required parameter: helpRequesterEmail")
          .serialize();
    }
    boolean paired = helpRequesterQueue.checkPaired(debuggingPartnerName, helpRequesterName);
    if (!paired) {
      return new FailureResponse(
          "error_bad_request", "Debugging Partner " + debuggingPartnerName + " with email " +
          debuggingPartnerEmail + " is not paired with Help Requester " + helpRequesterName + " with email " + helpRequesterEmail)
          .serialize();
    }
    boolean flagSuccess = debuggingPartnerQueue.removeAndFlagDebuggingPartner(debuggingPartnerName, debuggingPartnerEmail);
    boolean rematchSuccess = helpRequesterQueue.moveBackToQueue(helpRequesterName, helpRequesterEmail);
    if (!flagSuccess) {
      return new FailureResponse(
          "error_bad_request", "Debugging Partner " + debuggingPartnerName + " with email " + debuggingPartnerEmail + " not found in queue.")
          .serialize();
    } else if(!rematchSuccess) {
      return new FailureResponse(
          "error_bad_request", "Help Requester " + helpRequesterName + " with email " + helpRequesterEmail +  "not found in queue.")
          .serialize();
    } else {
      return new SuccessResponse("success", "Debugging Partner " + debuggingPartnerName +
          " has been flagged, and Help Requester " + helpRequesterName + " has been moved back to the queue.").serialize();
    }
  }
}
