package edu.brown.cs32.livecode.dispatcher.handlers;

import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.SuccessResponse;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is a handler to determine if a user is a TA by comparing the email of the user to the
 * emails on the CSV of TAs.
 */
public class IsInstructorHandler implements Route {

  private final List<List<String>> listInstructors;

  public IsInstructorHandler(List<List<String>> listInstructors) {
    this.listInstructors = listInstructors;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    // request parameter - just email since unique for every person
    String email = request.queryParams("email");
    if (email == null) {
      return new FailureResponse("error_bad_request", "Missing required parameter: email")
          .serialize();
    }

    // parse email to make lowercase and remove leading/trailing zeros for comparison
    String emailParsed = email.toLowerCase().trim();

    // for every instructor on csv check if email matches passed in email
    for (List<String> instructor : this.listInstructors) {
      String instructorEmailParsed = instructor.get(1).toLowerCase().trim();

      // if matches, return that user is instructor
      if (instructorEmailParsed.equals(emailParsed)) {
        return new SuccessResponse("success", "instructor").serialize();
      }
    }
    // if does not match, return that user is student
    return new SuccessResponse("success", "student").serialize();
  }
}
