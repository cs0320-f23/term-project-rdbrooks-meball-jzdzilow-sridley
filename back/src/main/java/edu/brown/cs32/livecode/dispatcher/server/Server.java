package edu.brown.cs32.livecode.dispatcher.server;

import static spark.Spark.after;
import static spark.Spark.before;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.*;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.io.BufferedReader;
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
    before(
        (requester, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
          // response.header("Access-Control-Allow-Headers", "Content-Type");
        });

    after(
        (request, response) -> {
          // commented this out for being able to download info
          // response.header("Access-Control-Allow-Origin", "*");
          // response.header("Access-Control-Allow-Methods", "*");
        });

    // parse csv of instructors (to avoid repetition everytime endpoint called)
    List<List<String>> listInstructors =
        new ArrayList<>(Server.parseCsvInstructors()); // defensive copy

    // Setting up the handler for the GET endpoints
    Spark.get("addHelpRequester", new AddHelpRequesterHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "addDebuggingPartner", new AddDebuggingPartnerHandler(debuggingPartnerQueue, sessionState));
    Spark.get("helpRequesterDone", new HelpRequesterDoneHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "debuggingPartnerDone",
        new DebuggingPartnerDoneHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get(
        "getInfo", new GetInfoHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get("escalate", new EscalateHandler(helpRequesterQueue, sessionState));
    Spark.get(
        "flagAndRematch",
        new FlagAndRematchHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get(
        "session", new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
    Spark.get("downloadInfo", new DownloadInfoHandler(sessionState));
    Spark.get("isInstructor", new IsInstructorHandler(listInstructors));
    Spark.get("submitDebuggingQuestions", new SubmitDebuggingQuestionsHandler(sessionState));

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
        dispatcher.dispatch(sessionState, false);
      }
      System.out.print("");
    }
  }

  /**
   * This method parses the csv of instructors into a list of lists. It is done in the Server class
   * so this action is not repeated everytime somebody calls the isInstructor endpoint.
   *
   * @return parsed csv
   */
  public static List<List<String>> parseCsvInstructors() {
    try {
      BufferedReader bufferedFile =
          new BufferedReader(new FileReader("./data/instructor-list.csv"));
      List<List<String>> parsed = new ArrayList<>();

      String line = bufferedFile.readLine();
      // while there are lines left to read
      while (line != null) {
        // regex that splits on commas (from CSV sprint)
        String[] splitRowArray = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
        List<String> splitRowList = Arrays.asList(splitRowArray);
        parsed.add(splitRowList);

        // read the next line
        line = bufferedFile.readLine();
      }
      return parsed;
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
      // decided to print error message as relevant to developer
      System.exit(0);
    }
    return null; // required return statement outside of block
  }
}
