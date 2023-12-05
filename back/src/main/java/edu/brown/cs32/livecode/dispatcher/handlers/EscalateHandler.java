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
    String helpRequesterName = request.queryParams("helpRequesterName");
    String helpRequesterEmail = request.queryParams("helpRequesterEmail");
    if (helpRequesterName == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: helpRequesterName")
          .serialize();
    } else if (helpRequesterEmail == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: helpRequesterEmail")
          .serialize();
    }
    boolean setSuccess = helpRequesterQueue.setEscalated(helpRequesterName, helpRequesterEmail);
    if (setSuccess) {
      return new SuccessResponse(
          "success", "Help Requester " + helpRequesterName + " has been escalated!")
          .serialize();
    } else {
      return new FailureResponse(
          "error_bad_request", "Help Requester " + helpRequesterName +
          " with email " + helpRequesterEmail + " not found in queue.")
          .serialize();
    }
  }
}
