package edu.brown.cs32.livecode.dispatcher.server;

import static spark.Spark.after;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.*;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.util.ArrayList;
import java.util.List;
import spark.Spark;

/**
 * This class is the Server class that handles incoming requests to the endpoints as well as
 * creating the Dispatcher to pair DebuggingPartners with HelpRequesters.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class Server {
  static final int port = 3333;
  static SessionState sessionState;

  /**
   * Constructor for the Server class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   */
  public Server(
      HelpRequesterQueue helpRequesterQueue, DebuggingPartnerQueue debuggingPartnerQueue) {
    this.sessionState = new SessionState(false);
    Spark.port(port);
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET endpoints
    Spark.get("addHelpRequester", new AddHelpRequesterHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "addDebuggingPartner", new AddDebuggingPartnerHandler(debuggingPartnerQueue, sessionState));
    Spark.get("helpRequesterDone", new HelpRequesterDoneHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "debuggingPartnerDone",
        new DebuggingPartnerDoneHandler(debuggingPartnerQueue, sessionState));
    Spark.get(
        "getInfo", new GetInfoHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get("escalate", new EscalateHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "flagAndRematch",
        new FlagAndRematchHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get(
        "session", new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get("info", new DownloadInfoHandler(sessionState));
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * The main method that creates an instance of the Server class and the HoursDispatcher class
   *
   * @param args array of String arguments to main (unused)
   */
  public static void main(String[] args) {

    List<HelpRequester> queue = new ArrayList<>();

    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(queue);

    List<DebuggingPartner> currentDebuggingPartners = new ArrayList<>();
    DebuggingPartnerQueue debuggingPartnerQueue =
        new DebuggingPartnerQueue(currentDebuggingPartners);

    HoursDispatcher dispatcher =
        new HoursDispatcher(helpRequesterQueue, debuggingPartnerQueue, "Concurrency");

    Server server = new Server(helpRequesterQueue, debuggingPartnerQueue);

    while (true) {
      if (sessionState.getRunning()) {
        dispatcher.dispatch(sessionState);
      }
      System.out.print("");
    }
  }
}
