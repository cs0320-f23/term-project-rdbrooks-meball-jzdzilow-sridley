package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.sessionState.CsvWriter;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;
import spark.Request;
import spark.Response;
import spark.Route;

public class SessionHandler implements Route {
  private HelpRequesterQueue helpRequesterQueue;
  private DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  public SessionHandler(HelpRequesterQueue helpRequesterQueue, DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String command = request.queryParams("command");
    if (command == null) {
      return new FailureResponse(
          "error_bad_request", "Missing required parameter: command")
          .serialize();
    } else if (command.equals("end")) {
      if (sessionState.getRunning()) {
        sessionState.setEndTime(Utils.date());
        CsvWriter writer = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
        writer.write();
        sessionState.setRunning(false);
        return new SuccessResponse("Ended session!").serialize();
      } else {
        return new FailureResponse(
            "error_bad_request", "Cannot end if no session is running.")
            .serialize();
      }
    } else if (command.equals("begin")) {
      if (!sessionState.getRunning()) {
        sessionState.setBeginTime(Utils.date());
        sessionState.setRunning(true);
        helpRequesterQueue.reset();
        debuggingPartnerQueue.reset();
        return new SuccessResponse("Began new session!").serialize();
      }
      else {
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
