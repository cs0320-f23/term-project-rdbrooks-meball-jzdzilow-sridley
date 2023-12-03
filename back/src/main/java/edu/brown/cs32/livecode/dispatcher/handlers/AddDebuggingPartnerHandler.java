package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddDebuggingPartnerHandler implements Route {
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  public AddDebuggingPartnerHandler(
      DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String name = request.queryParams("name");
    String email = request.queryParams("email");
    if (name == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: name")
          .serialize();
    } else if (email == null) {
      return new FailureResponse(
          "error_bad_request", "Missing required parameter: email")
          .serialize();
    }
    debuggingPartnerQueue.addDebuggingPartner(new DebuggingPartner(name, email));
    return new SuccessResponse("success", "Debugging Partner " + name + " was added to the queue.")
        .serialize();
  }
}
