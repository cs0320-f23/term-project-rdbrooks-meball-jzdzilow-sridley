package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the AddDebuggingPartnerHandler class, implements the Route interface such that it
 * can be attached to the endpoint /addDebuggingPartner.
 *
 * <p>A call to the /addDebuggingPartner endpoint adds a new DebuggingPartner to the queue.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class AddDebuggingPartnerHandler implements Route {
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  /**
   * Constructor for the AddDebuggingPartnerHandler class
   *
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param sessionState SessionState representing the current state of the session
   */
  public AddDebuggingPartnerHandler(
      DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /addDebuggingPartner endpoint
   *
   * @param request Request object containing parameters
   * @param response Response object that is unused
   * @return Json Object containing result of this request
   */
  @Override
  public Object handle(Request request, Response response) {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String name = request.queryParams("name");
    String email = request.queryParams("email");
    if (name == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: name")
          .serialize();
    } else if (email == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: email")
          .serialize();
    }
    debuggingPartnerQueue.addDebuggingPartner(new DebuggingPartner(name, email));
    return new SuccessResponse("success", "Debugging Partner " + name + " was added to the queue.")
        .serialize();
  }
}
