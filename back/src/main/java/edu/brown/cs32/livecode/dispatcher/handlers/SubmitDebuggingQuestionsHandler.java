package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;
import java.io.FileWriter;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the SubmitDebuggingQuestionsHandler, which handles and records submissions to the
 * debugging partner questions, by default recording them in debugging-process-answers.csv
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class SubmitDebuggingQuestionsHandler implements Route {

  private SessionState sessionState;

  /**
   * Constructor for the SubmitDebuggingQuestionsHandler class
   *
   * @param sessionState SessionState managing info about the running session
   */
  public SubmitDebuggingQuestionsHandler(SessionState sessionState) {
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /submitDebuggingQuestions endpoint
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
    String helpRequesterName = request.queryParams("helpRequesterName");
    String helpRequesterEmail = request.queryParams("helpRequesterEmail");
    String debuggingPartnerName = request.queryParams("debuggingPartnerName");
    String debuggingPartnerEmail = request.queryParams("debuggingPartnerEmail");
    String bugCategory = request.queryParams("bugCategory");
    String debuggingProcess = request.queryParams("debuggingProcess");
    if (helpRequesterName == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: helpRequesterName")
          .serialize();
    } else if (helpRequesterEmail == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: helpRequesterEmail")
          .serialize();
    } else if (debuggingPartnerName == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: debuggingPartnerName")
          .serialize();
    } else if (debuggingPartnerEmail == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: debuggingPartnerEmail")
          .serialize();
    } else if (bugCategory == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: bugCategory")
          .serialize();
    } else if (debuggingProcess == null) {
      return new FailureResponse(
              "error_bad_request", "Missing required parameter: debuggingProcess")
          .serialize();
    } else {
      FileWriter writer = new FileWriter(sessionState.getAnswersFileName(), true);
      String toWrite =
          helpRequesterName
              + ", "
              + helpRequesterEmail
              + ", "
              + debuggingPartnerName
              + ", "
              + debuggingPartnerEmail
              + ", "
              + sessionState.getBeginTime()
              + ", "
              + Utils.simpleTime()
              + ", \""
              + bugCategory
              + "\", \""
              + debuggingProcess
              + "\"\n";
      writer.write(toWrite);
      writer.close();
      return new SuccessResponse(
              "Debugging questions recorded for Debugging Partner "
                  + debuggingPartnerName
                  + " and Help Requester "
                  + helpRequesterName)
          .serialize();
    }
  }
}
