package edu.brown.cs32.livecode.dispatcher.server;

import static spark.Spark.after;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.*;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    // parse csv of TAs (to avoid repetition everytime endpoint called)
    List<List<String>> listTAs = new ArrayList<>(Server.parseCsvTA()); // making defensive copy

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
    Spark.get("isTA", new IsTAHandler(listTAs));

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

  /**
   * Method to parse the csv of TAs into a list of lists.
   * @return parsed csv
   */
  private static List<List<String>> parseCsvTA() {
    try {
      BufferedReader bufferedFile = new BufferedReader(new FileReader("./data/ta-list.csv"));

      List<List<String>> parsed = new ArrayList<>();

      String line = bufferedFile.readLine();
      // while there are lines left to read
      while (line != null) {
        String[] splitRowArray = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
        List<String> splitRowList = Arrays.asList(splitRowArray);
        parsed.add(splitRowList);

        // read the next line
        line = bufferedFile.readLine();
      }
      return parsed;
    }
    catch (IOException e){
      System.out.println("ERROR: " + e.getMessage());
      // decided to print error message as relevant to developer
      System.exit(0);
    }
    return null; // required return statement outside of block
  }
}
