package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the AddHelpRequesterHandler class, implements the Route interface such that it can
 * be attached to the endpoint /addHelpRequester.
 *
 * <p>A call to the /addHelpRequester endpoint adds a new DebuggingPartner to the queue.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class AddHelpRequesterHandler implements Route {
  private final HelpRequesterQueue helpRequesterQueue;
  private SessionState sessionState;

  /**
   * Constructor for the AddHelpRequesterHandler class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param sessionState SessionState representing the current state of the session
   */
  public AddHelpRequesterHandler(HelpRequesterQueue helpRequesterQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /addHelpRequester endpoint
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
    String bugType = request.queryParams("bugType");
    if (name == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: name")
          .serialize();
    } else if (email == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: email")
          .serialize();
    } else if (bugType == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: bugType")
          .serialize();
    }
    helpRequesterQueue.addNeedsHelp(new HelpRequester(name, email, bugType));
    System.out.println("bug type: " + bugType);
    return new SuccessResponse("success", "Help Requester " + name + " was added to the queue.")
        .serialize();
  }

  /**
   * Record representing a failed request
   *
   * @param result String brief error message
   * @param error_message String verbose error message
   */
  public record FailureResponse(String result, String error_message) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FailureResponse.class).toJson(this);
    }
  }

  /**
   * Record representing a successful request
   *
   * @param result String brief success message
   * @param message String verbose success message
   */
  public record SuccessResponse(String result, String message) {
    public SuccessResponse(String message) {
      this("success", message);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SuccessResponse.class).toJson(this);
    }
  }
}
