package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

public class EscalateHandler implements Route {

  private HelpRequesterQueue helpRequesterQueue;
  private SessionState sessionState;

  public EscalateHandler(HelpRequesterQueue helpRequesterQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String name = request.queryParams("helpRequester");
    if (name == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: helpRequester")
          .serialize();
    }
    boolean setSuccess = helpRequesterQueue.setEscalated(name);
    if (setSuccess) {
      return new SuccessResponse(
          "success", "Help Requester " + name + " has been escalated!")
          .serialize();
    } else {
      return new FailureResponse(
          "error_bad_request", "Help Requester " + name + " not found in queue.")
          .serialize();
    }
  }
}