package edu.brown.cs32.livecode.dispatcher.server;

import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.AddDebuggingPartnerHandler;
import edu.brown.cs32.livecode.dispatcher.handlers.AddHelpRequesterHandler;
import edu.brown.cs32.livecode.dispatcher.handlers.DebuggingPartnerDoneHandler;
import edu.brown.cs32.livecode.dispatcher.handlers.GetInfoHandler;
import edu.brown.cs32.livecode.dispatcher.handlers.HelpRequesterDoneHandler;
import edu.brown.cs32.livecode.dispatcher.handlers.SessionHandler;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import java.util.ArrayList;
import java.util.List;
import static spark.Spark.after;

import spark.Spark;

/**
 * This is an extended version of the prior queue-manager example.
 * Here we focus on concurrency, not on defensive programming.
 */
public class Server {

    static final int port = 3333;
    static SessionState sessionState;

    public Server(HelpRequesterQueue helpRequesterQueue, DebuggingPartnerQueue debuggingPartnerQueue) {
        this.sessionState = new SessionState(false);
        Spark.port(port);
        after(
            (request, response) -> {
                response.header("Access-Control-Allow-Origin", "*");
                response.header("Access-Control-Allow-Methods", "*");
            });

        // Setting up the handler for the GET endpoints
        Spark.get("addHelpRequester", new AddHelpRequesterHandler(helpRequesterQueue, sessionState));
        Spark.get("addDebuggingPartner", new AddDebuggingPartnerHandler(debuggingPartnerQueue, sessionState));
        Spark.get("helpRequesterDone", new HelpRequesterDoneHandler(helpRequesterQueue, sessionState));
        Spark.get("debuggingPartnerDone", new DebuggingPartnerDoneHandler(debuggingPartnerQueue, sessionState));
        Spark.get("getInfo", new GetInfoHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
        Spark.get("session", new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState));
        Spark.init();
        Spark.awaitInitialization();
    }

    public static void main(String[] args) {

        List<HelpRequester> queue = new ArrayList<>();

        HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(queue);

        List<DebuggingPartner> currentDebuggingPartners = new ArrayList<>();
        DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(
            currentDebuggingPartners);

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
