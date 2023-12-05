package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class IsTAHandler implements Route {

  private final List<List<String>> listTAs;

  public IsTAHandler(List<List<String>> listTAs) {
    this.listTAs = listTAs;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String email = request.queryParams("email");

    for (List<String> ta : listTAs) {
      if (ta.get(1).equals(email)) {
        return new SuccessResponse("success", "instructor").serialize();
      }
    }
    return new SuccessResponse("success", "student").serialize();
  }
}
