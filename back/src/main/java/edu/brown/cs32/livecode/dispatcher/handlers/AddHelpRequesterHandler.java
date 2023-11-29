package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddHelpRequesterHandler implements Route {
  private final HelpRequesterQueue helpRequesterQueue;
  private SessionState sessionState;

  public AddHelpRequesterHandler(HelpRequesterQueue helpRequesterQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse(
          "error_bad_request", "No session is running.")
          .serialize();
    }
    String name = request.queryParams("helpRequester");
    if (name == null) {
      return new FailureResponse(
          "error_bad_request", "Missing required parameter: helpRequester")
          .serialize();
    }
    helpRequesterQueue.addNeedsHelp(new HelpRequester(name));
    return new SuccessResponse(
        "success", "Help Requester " + name + " was added to the queue.")
        .serialize();
  }

  public record FailureResponse(String result, String error_message) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FailureResponse.class).toJson(this);
    }
  }

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
