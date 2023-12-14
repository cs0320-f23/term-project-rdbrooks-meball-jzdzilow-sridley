package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the DownloadInfoHandler class, implements the Route interface such that it can be
 * attached to the endpoint /info.
 *
 * <p>A call to the /info endpoint allows a download of the attendance information.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class DownloadInfoHandler implements Route {

  private SessionState sessionState;

  /**
   * Constructor for the DownloadInfoHandler class
   *
   * @param sessionState SessionState representing the current state of the session
   */
  public DownloadInfoHandler(SessionState sessionState) {
    this.sessionState = sessionState;
  }

  /**
   * Handler for a call to the /info endpoint
   *
   * @param request Request object containing parameters
   * @param response Response object that is unused
   * @return Json Object containing result of this request
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (sessionState.getRunning()) {
      return new FailureResponse(
              "error_bad_request", "Download data after session has finished running.")
          .serialize();
    }
    String info = request.queryParams("type");
    if (info == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: type")
          .serialize();
    }
    if (info.equals("all")) {
      String filePath = "data/all-attendance.csv";
      try (OutputStream outputStream = response.raw().getOutputStream()) {
        byte[] content = Files.readAllBytes(Paths.get(filePath));
        response.header("Content-Disposition", "attachment; filename=all-attendance.csv");
        response.header("Content-Type", "text/csv");
        response.status(200);
        outputStream.write(content);
      } catch (Exception e) {
        return new FailureResponse("error_bad_request", "File could not be downloaded").serialize();
      }
    }
    if (info.equals("debugging")) {
      try (OutputStream outputStream = response.raw().getOutputStream()) {
        String beginTime = this.sessionState.getBeginTime();
        String filePath = "data/sessions/debugging-partner-attendance-" + beginTime + ".csv";
        byte[] content = Files.readAllBytes(Paths.get(filePath));
        response.header(
            "Content-Disposition",
            "attachment; filename=debugging-attendance-" + beginTime + ".csv");
        response.status(200);
        outputStream.write(content);
      } catch (Exception e) {
        return new FailureResponse("error_bad_request", "File could not be downloaded").serialize();
      }
    }

    if (info.equals("helpRequester")) {
      String beginTime = this.sessionState.getBeginTime();
      String filePath = "data/sessions/help-requester-attendance-" + beginTime + ".csv";

      byte[] content = Files.readAllBytes(Paths.get(filePath));
      response.header(
          "Content-Disposition",
          "attachment; filename=help-requester-attendance-" + beginTime + ".csv");
      response.status(200);
      try (OutputStream outputStream = response.raw().getOutputStream()) {
        outputStream.write(content);
      } catch (Exception e) {
        return new FailureResponse("error_bad_request", "File could not be downloaded").serialize();
      }
    }
    return new FailureResponse(
            "error_bad_request", "Please enter what type of info you would like to download")
        .serialize();
  }
}
