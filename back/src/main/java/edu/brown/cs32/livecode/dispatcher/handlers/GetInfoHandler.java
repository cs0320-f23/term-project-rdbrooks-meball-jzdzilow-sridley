package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is the GetInfoHandler class, implements the Route interface such that it can be
 * attached to the endpoint /getInfo.
 *
 * <p>A call to the /getInfo endpoint allows the frontend to get information about the entire
 * session or about a specific DebuggingPartner or HelpRequester.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class GetInfoHandler implements Route {

  private HelpRequesterQueue helpRequesterQueue;
  private DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  /**
   * Constructor for the GetInfoHandler class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param sessionState SessionState representing the current state of the session
   */
  public GetInfoHandler(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  /**
   * Helper function that accesses all information about the current Collab Hours Session
   *
   * @return Json Object containing result of this request
   */
  public Object getAllInfo() {
    List<HelpRequester> waitingHelpRequesters = helpRequesterQueue.getNeedHelpList();
    List<List<String>> waitingHRQs = new ArrayList<>();
    for (HelpRequester helpRequester : waitingHelpRequesters) {
      waitingHRQs.add(
          List.of(
              helpRequester.getName(), helpRequester.getEmail(), helpRequester.getJoinedTime()));
    }

    List<HelpRequester> pairedHelpRequesters = helpRequesterQueue.getGettingHelpList();
    List<List<List<String>>> pairs = new ArrayList<>();
    List<List<List<String>>> escalatedPairs = new ArrayList<>();
    List<List<List<String>>> nonEscalatedPairs = new ArrayList<>();

    for (HelpRequester helpRequester : pairedHelpRequesters) {
      DebuggingPartner helper = helpRequester.getDebuggingPartner();
      pairs.add(
          List.of(
              List.of(helper.getName(), helper.getEmail()),
              List.of(helpRequester.getName(), helpRequester.getEmail()),
              List.of(helper.getPairedAtTime())));
      if (helpRequester.getEscalated()) {
        escalatedPairs.add(
            List.of(
                List.of(helper.getName(), helper.getEmail()),
                List.of(helpRequester.getName(), helpRequester.getEmail()),
                List.of(helper.getPairedAtTime())));
      } else {
        nonEscalatedPairs.add(
            List.of(
                List.of(helper.getName(), helper.getEmail()),
                List.of(helpRequester.getName(), helpRequester.getEmail()),
                List.of(helper.getPairedAtTime())));
      }
    }

    List<DebuggingPartner> debuggingPartners = debuggingPartnerQueue.getDebuggingPartnerList();

    List<List<String>> openDBPs = new ArrayList<>();
    for (DebuggingPartner debuggingPartner : debuggingPartners) {
      if (debuggingPartner.isFree()) {
        openDBPs.add(
            List.of(
                debuggingPartner.getName(),
                debuggingPartner.getEmail(),
                debuggingPartner.getJoinedTime()));
      }
    }

    List<HelpRequester> helped = helpRequesterQueue.getHelpedList();
    List<List<String>> helpedNames = new ArrayList<>();
    for (HelpRequester helpRequester : helped) {
      helpedNames.add(
          List.of(
              helpRequester.getName(), helpRequester.getEmail(), helpRequester.getJoinedTime()));
    }

    return new AllInfoSuccessResponse(
            "Here is the waiting Help Requester queue, open Debugging Partner queue, current pairings, and past Help Requesters!",
            waitingHRQs,
            openDBPs,
            pairs,
            escalatedPairs,
            nonEscalatedPairs,
            helpedNames)
        .serialize();
  }

  /**
   * Helper function that accesses all information about the current Collab Hours Session
   *
   * @param targetName String representing the name of the student to get info for
   * @param role String representing role of the student to get info for
   * @param targetEmail String representing the email of the student to get info for
   * @return Json Object containing result of this request
   */
  public Object getSpecificInfo(String targetName, String role, String targetEmail) {
    if (role.equals("debuggingPartner")) {
      List<DebuggingPartner> debuggingPartners = debuggingPartnerQueue.getAllDebuggingPartnerList();
      for (DebuggingPartner debuggingPartner : debuggingPartners) {
        String name = debuggingPartner.getName();
        String email = debuggingPartner.getEmail();
        if (name.equals(targetName) && email.equals(targetEmail)) {
          HelpRequester currentlyHelping = debuggingPartner.getCurrentHelpRequester();
          String helpingName = "";
          if (currentlyHelping != null) {
            helpingName = currentlyHelping.getName();

            return new DebuggingPartnerInfoSuccessResponse(
                    "Debugging Partner " + targetName + " found!",
                    helpingName,
                    currentlyHelping.getEmail(),
                    currentlyHelping.getBugType(),
                    debuggingPartner)
                .serialize();
          } else {
            return new DebuggingPartnerInfoSuccessResponse(
                    "Debugging Partner " + targetName + " found!",
                    helpingName,
                    "",
                    "",
                    debuggingPartner)
                .serialize();
          }
        }
      }
    } else if (role.equals("helpRequester")) {
      List<HelpRequester> helpRequesters = helpRequesterQueue.getAllHelpRequesters();
      for (HelpRequester helpRequester : helpRequesters) {
        String name = helpRequester.getName();
        String email = helpRequester.getEmail();
        if (name.equals(targetName) && email.equals(targetEmail)) {
          DebuggingPartner gettingHelpFrom = helpRequester.getDebuggingPartner();
          String helpFromName = "";
          if (gettingHelpFrom != null) {
            helpFromName = gettingHelpFrom.getName();
          }
          return new HelpRequesterInfoSuccessResponse(
                  "Help Requester " + targetName + " found!", helpFromName, helpRequester)
              .serialize();
        }
      }
    }
    return new FailureResponse(
            "error_bad_request",
            "No " + role + " found named " + targetName + " with email " + targetEmail)
        .serialize();
  }

  /**
   * Handler for a call to the /flagAndRematch endpoint
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
    String role = request.queryParams("role");
    String email = request.queryParams("email");
    if (name == null || role == null || email == null) {
      return getAllInfo();
    } else {
      return getSpecificInfo(name, role, email);
    }
  }

  /**
   * Record representing a success response to request for all info
   *
   * @param result String brief success response
   * @param message String verbose success response
   * @param waitingHRQs list of String of names of waiting HelpRequesters
   * @param openDBPs list of String of free DebuggingPartners
   * @param pairs nested list of String of pairs (DP first, then HR)
   * @param escalatedPairs nested list of String of escalated pairs (DP first, then HR)
   * @param escalatedPairs nested list of String of non-escalated pairs (DP first, then HR)
   * @param helpedNames list of String of helped HelpRequesters
   */
  public record AllInfoSuccessResponse(
      String result,
      String message,
      List<List<String>> waitingHRQs,
      List<List<String>> openDBPs,
      List<List<List<String>>> pairs,
      List<List<List<String>>> escalatedPairs,
      List<List<List<String>>> nonEscalatedPairs,
      List<List<String>> helpedNames) {
    public AllInfoSuccessResponse(
        String message,
        List<List<String>> waitingHRQs,
        List<List<String>> openDBPs,
        List<List<List<String>>> pairs,
        List<List<List<String>>> escalatedPairs,
        List<List<List<String>>> nonEscalatedPairs,
        List<List<String>> helpedNames) {
      this(
          "success",
          message,
          waitingHRQs,
          openDBPs,
          pairs,
          escalatedPairs,
          nonEscalatedPairs,
          helpedNames);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(AllInfoSuccessResponse.class).toJson(this);
    }
  }

  /**
   * Record representing a success request for accessing info of a DebuggingPartner
   *
   * @param result String brief success message
   * @param message String verbose success message
   * @param name String representing DebuggingPartner's name
   * @param helpRequesterName String representing current HelpRequester's name
   * @param flagged boolean representing whether DebuggingPartner was flagged
   * @param studentsHelped int representing how many students the DebuggingPartner has helped
   */
  public record DebuggingPartnerInfoSuccessResponse(
      String result,
      String message,
      String name,
      String email,
      String joinedTime,
      String pairedAtTime,
      String helpRequesterName,
      String helpRequesterEmail,
      String helpRequesterBug,
      boolean flagged,
      int studentsHelped) {
    public DebuggingPartnerInfoSuccessResponse(
        String message,
        String currentlyHelping,
        String helpRequesterEmail,
        String helpRequesterBug,
        DebuggingPartner debuggingPartner) {
      this(
          "success",
          message,
          debuggingPartner.getName(),
          debuggingPartner.getEmail(),
          debuggingPartner.getJoinedTime(),
          debuggingPartner.getPairedAtTime(),
          currentlyHelping,
          helpRequesterEmail,
          helpRequesterBug,
          debuggingPartner.getFlagged(),
          debuggingPartner.getStudentsHelped());
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(DebuggingPartnerInfoSuccessResponse.class).toJson(this);
    }
  }

  /**
   * Record representing a successful request for accessing info of a HelpRequester
   *
   * @param result String brief success message
   * @param message String verbose success message
   * @param name String representing the HelpRequester's name
   * @param debuggingPartnerName String representing the DebuggingPartner helping this student
   */
  public record HelpRequesterInfoSuccessResponse(
      String result,
      String message,
      String name,
      String email,
      String joinedTime,
      String pairedAtTime,
      String debuggingPartnerName,
      String bugType,
      boolean escalated,
      boolean debugged) {
    public HelpRequesterInfoSuccessResponse(
        String message, String debuggingPartnerName, HelpRequester helpRequester) {
      this(
          "success",
          message,
          helpRequester.getName(),
          helpRequester.getEmail(),
          helpRequester.getJoinedTime(),
          helpRequester.getPairedAtTime(),
          debuggingPartnerName,
          helpRequester.getBugType(),
          helpRequester.getEscalated(),
          helpRequester.getDebugged());
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(HelpRequesterInfoSuccessResponse.class).toJson(this);
    }
  }
}
