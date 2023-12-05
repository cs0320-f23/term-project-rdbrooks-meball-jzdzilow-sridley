package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.CsvWriter;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the SessionHandler class, implements the Route interface such that it can be
 * attached to the endpoint /session.
 *
 * <p>A call to the /session endpoint allows a HelpRequester to leave the queue once they are done
 * getting debugged.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class SessionHandler implements Route {
  private final HelpRequesterQueue helpRequesterQueue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private final SessionState sessionState;

  /**
   * Constructor for the SessionHandler class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param sessionState SessionState representing the current state of the session
   */
  public SessionHandler(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /session endpoint
   *
   * @param request Request object containing parameters
   * @param response Response object that is unused
   * @return Json Object containing result of this request
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String command = request.queryParams("command");
    if (command == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: command")
          .serialize();
    } else if (command.equals("end")) {
      if (sessionState.getRunning()) {
        sessionState.setEndTime(Utils.date());
        CsvWriter writer = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
        writer.write();
        sessionState.setRunning(false);
        return new SuccessResponse("Ended session!").serialize();
      } else {
        return new FailureResponse("error_bad_request", "Cannot end if no session is running.")
            .serialize();
      }
    } else if (command.equals("begin")) {
      if (!sessionState.getRunning()) {
        sessionState.setBeginTime(Utils.date());
        sessionState.setRunning(true);
        helpRequesterQueue.reset();
        debuggingPartnerQueue.reset();
        return new SuccessResponse("Began new session!").serialize();
      } else {
        return new FailureResponse(
                "error_bad_request", "Cannot begin if session is already running.")
            .serialize();
      }
    } else {
      return new FailureResponse(
              "error_bad_request", "Required parameter command must be end or begin.")
          .serialize();
    }
  }
}
