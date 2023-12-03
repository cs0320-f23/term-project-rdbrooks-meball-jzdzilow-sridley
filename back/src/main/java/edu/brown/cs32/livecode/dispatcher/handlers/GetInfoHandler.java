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

public class GetInfoHandler implements Route {

  private HelpRequesterQueue helpRequesterQueue;
  private DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  public GetInfoHandler(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  public Object getAllInfo() {
    List<HelpRequester> waitingHelpRequesters = helpRequesterQueue.getNeedHelpList();
    List<String> waitingHRQs = new ArrayList<>();
    for (HelpRequester helpRequester : waitingHelpRequesters) {
      waitingHRQs.add(helpRequester.getName());
    }

    List<HelpRequester> pairedHelpRequesters = helpRequesterQueue.getGettingHelpList();
    List<String> pairs = new ArrayList<>();
    List<String> escalatedPairs = new ArrayList<>();
    for (HelpRequester helpRequester : pairedHelpRequesters) {
      DebuggingPartner helper = helpRequester.getDebuggingPartner();
      pairs.add(
          "Debugging Partner: " + helper.getName() + ", Help Requester: " + helpRequester.getName());
      if (helpRequester.getEscalated()) {
        escalatedPairs.add("Debugging Partner: " + helper.getName() + ", Help Requester: " + helpRequester.getName());
      }
    }

    List<DebuggingPartner> allDebuggingPartners =
        debuggingPartnerQueue.getAllDebuggingPartnerList();
    List<String> openDBPs = new ArrayList<>();
    for (DebuggingPartner debuggingPartner : allDebuggingPartners) {
      if (debuggingPartner.isFree()) {
        openDBPs.add(debuggingPartner.getName());
      }
    }

    List<HelpRequester> helped = helpRequesterQueue.getHelpedList();
    List<String> helpedNames = new ArrayList<>();
    for (HelpRequester helpRequester : helped) {
      helpedNames.add(helpRequester.getName());
    }

    return new AllInfoSuccessResponse(
        "Here is the waiting Help Requester queue, open Debugging Partner queue, current pairings, and past Help Requesters!",
        waitingHRQs,
        openDBPs,
        pairs,
        escalatedPairs,
        helpedNames)
        .serialize();
  }

  public Object getSpecificInfo(String targetName, String role) {
    if (role.equals("debuggingPartner")) {
      List<DebuggingPartner> debuggingPartners = debuggingPartnerQueue.getAllDebuggingPartnerList();
      for (DebuggingPartner debuggingPartner : debuggingPartners) {
        String name = debuggingPartner.getName();
        if (name.equals(targetName)) {
          HelpRequester currentlyHelping = debuggingPartner.getCurrentHelpRequester();
          String helpingName = "";
          if (currentlyHelping != null) {
            helpingName = currentlyHelping.getName();
          }
          return new DebuggingPartnerInfoSuccessResponse("Debugging Partner " + targetName + " found!",
              targetName, helpingName, debuggingPartner.getFlagged(), debuggingPartner.getStudentsHelped()).serialize();
        }
      }
    } else {
      List<HelpRequester> helpRequesters = helpRequesterQueue.getAllHelpRequesters();
      for (HelpRequester helpRequester : helpRequesters) {
        String name = helpRequester.getName();
        if (name.equals(targetName)) {
          DebuggingPartner gettingHelpFrom = helpRequester.getDebuggingPartner();
          String helpFromName = "";
          if (gettingHelpFrom != null) {
            helpFromName = gettingHelpFrom.getName();
          }
          return new HelpRequesterInfoSuccessResponse("Help Requester " + targetName + " found!",
              targetName, helpFromName).serialize();
        }
      }
    }
    return new FailureResponse("error_bad_request", "No " + role + " found named " + targetName).serialize();
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse("error_bad_request", "No session is running.").serialize();
    }
    String name = request.queryParams("name");
    String role = request.queryParams("role");
    if (name == null || role == null) {
      return getAllInfo();
    } else {
      return getSpecificInfo(name, role);
    }
  }

  public record AllInfoSuccessResponse(
      String result,
      String message,
      List<String> waitingHRQs,
      List<String> openDBPs,
      List<String> pairs,
      List<String> escalatedPairs,
      List<String> helpedNames) {
    public AllInfoSuccessResponse(
        String message,
        List<String> waitingHRQs,
        List<String> openDBPs,
        List<String> pairs,
        List<String> escalatedPairs,
        List<String> helpedNames) {
      this("success", message, waitingHRQs, openDBPs, pairs, escalatedPairs, helpedNames);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(AllInfoSuccessResponse.class).toJson(this);
    }
  }

  public record DebuggingPartnerInfoSuccessResponse(
      String result,
      String message,
      String name,
      String helpRequesterName,
      boolean flagged,
      int studentsHelped) {
    public DebuggingPartnerInfoSuccessResponse(
        String message,
        String name,
        String helpRequesterName,
        boolean flagged,
        int studentsHelped) {
      this("success", message, name, helpRequesterName, flagged, studentsHelped);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(DebuggingPartnerInfoSuccessResponse.class).toJson(this);
    }
  }

  public record HelpRequesterInfoSuccessResponse(
      String result,
      String message,
      String name,
      String debuggingPartnerName) {
    public HelpRequesterInfoSuccessResponse(
        String message,
        String name,
        String debuggingPartnerName) {
      this("success", message, name, debuggingPartnerName);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(HelpRequesterInfoSuccessResponse.class).toJson(this);
    }
  }
}
