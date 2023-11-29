package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.Moshi;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler.FailureResponse;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

public class GetInfoHandler implements Route {

  private HelpRequesterQueue helpRequesterQueue;
  private DebuggingPartnerQueue debuggingPartnerQueue;
  private SessionState sessionState;

  public GetInfoHandler(HelpRequesterQueue helpRequesterQueue, DebuggingPartnerQueue debuggingPartnerQueue, SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    if (!sessionState.getRunning()) {
      return new FailureResponse(
          "error_bad_request", "No session is running.")
          .serialize();
    }
    List<HelpRequester> waitingHelpRequesters = helpRequesterQueue.getNeedHelpList();
    List<String> waitingHRQs = new ArrayList<>();
    for (HelpRequester helpRequester : waitingHelpRequesters) {
      waitingHRQs.add(helpRequester.getName());
    }

    List<HelpRequester> pairedHelpRequesters = helpRequesterQueue.getGettingHelpList();
    List<String> pairs = new ArrayList<>();
    for (HelpRequester helpRequester : pairedHelpRequesters) {
      DebuggingPartner helper = helpRequester.getDebuggingPartner();
      pairs.add("Debugging Partner " + helper.getName() + " + Help Requester " + helpRequester.getName());
    }

    List<DebuggingPartner> allDebuggingPartners = debuggingPartnerQueue.getAllDebuggingPartnerList();
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

    return new InfoSuccessResponse(
        "success", "Here is the waiting Help Requester queue, open Debugging Partner queue, current pairings, and past Help Requesters!",
        waitingHRQs, openDBPs, pairs, helpedNames)
        .serialize();
  }

  public record InfoSuccessResponse(String result, String message, List<String> waitingHRQs,
                                    List<String> openDBPs, List<String> pairs, List<String> helpedNames) {
    public InfoSuccessResponse(String message, List<String> waitingHRQs,
        List<String> openDBPs, List<String> pairs, List<String> helpedNames) {
      this("success", message, waitingHRQs, openDBPs, pairs, helpedNames);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(InfoSuccessResponse.class).toJson(this);
    }
  }
}