package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import spark.Request;
import spark.Response;
import spark.Route;

public class DebuggingPartnerDoneHandler implements Route {
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  public DebuggingPartnerDoneHandler(DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse(
          "error_bad_request", "No session is running.")
          .serialize();
    }
    String name = request.queryParams("debuggingPartner");
    if (name == null) {
      return new FailureResponse(
          "error_bad_request", "Missing required parameter: debuggingPartner")
          .serialize();
    }
    boolean setSuccess = debuggingPartnerQueue.removeDebuggingPartner(name);
    if (setSuccess) {
      return new SuccessResponse(
          "success", "Debugging Partner " + name + " has left!")
          .serialize();
    } else {
      return new FailureResponse(
          "error_bad_request", "Debugging Partner " + name + " not found in queue.")
          .serialize();
    }

  }
}
