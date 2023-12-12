package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the HelpRequesterDoneHandler class, implements the Route interface such that it can
 * be attached to the endpoint /helpRequesterDone.
 *
 * <p>A call to the /helpRequesterDone endpoint allows a HelpRequester to leave the queue once they
 * are done getting debugged.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class HelpRequesterDoneHandler implements Route {
  private final HelpRequesterQueue helpRequesterQueue;
  private SessionState sessionState;

  /**
   * Constructor for the HelpRequesterDoneHandler class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param sessionState SessionState representing the current state of the session
   */
  public HelpRequesterDoneHandler(
      HelpRequesterQueue helpRequesterQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /helpRequesterDone endpoint
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

    if (record != null && record.equals("no")) {
      helpRequesterQueue.removeFromAttendanceList(name, email);
    }

    boolean setSuccess = helpRequesterQueue.setDoneDebugging(name, email);

    if (setSuccess) {
      return new SuccessResponse(
              "success", "Help Requester " + name + " has been debugged and allowed to leave!")
          .serialize();
    } else {
      return new FailureResponse(
              "error_bad_request", "Help Requester " + name + " not found in queue.")
          .serialize();
    }
  }
}
