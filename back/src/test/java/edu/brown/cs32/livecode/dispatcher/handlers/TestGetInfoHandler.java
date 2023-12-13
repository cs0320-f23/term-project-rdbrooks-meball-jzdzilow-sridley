package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.handlers.GetInfoHandler.DebuggingPartnerInfoSuccessResponse;
import edu.brown.cs32.livecode.dispatcher.handlers.GetInfoHandler.HelpRequesterInfoSuccessResponse;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/**
 * This is a testing class that tests the GetInfoHandler class, which handles requests to the
 * /getInfo endpoint.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestGetInfoHandler {
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    SessionState sessionState = new SessionState(false);
    sessionState.setBeginTime(Utils.simpleTime());
    SessionHandler sessionHandler =
        new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    GetInfoHandler getInfoHandler =
        new GetInfoHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    Spark.get("/session", sessionHandler);
    Spark.get("/getInfo", getInfoHandler);
    Spark.awaitInitialization();
  }

  /** Resets the state after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/session");
    Spark.unmap("/getInfo");
    Spark.awaitStop();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (Note: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json");
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Testing with no session running
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testNoSession() throws IOException {
    HttpURLConnection addConnection = tryRequest("getInfo");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("No session is running.", body.get("error_message"));
    addConnection.disconnect();
  }

  /**
   * Testing general getInfo call
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testGeneral() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection getConnection = tryRequest("getInfo");
    Assertions.assertEquals(200, getConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(getConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals(
        "Here is the waiting Help Requester queue, open Debugging Partner queue, current pairings, and past Help Requesters!",
        body.get("message"));
    getConnection.disconnect();
  }

  /**
   * Testing the wrong role
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testWrongRole() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection getConnection = tryRequest("getInfo?role=clown");
    Assertions.assertEquals(200, getConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(getConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals(
        "Here is the waiting Help Requester queue, open Debugging Partner queue, current pairings, and past Help Requesters!",
        body.get("message"));
    getConnection.disconnect();
  }

  /**
   * Testing getting into for the debugging partner
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testDebugger() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection getConnection =
        tryRequest("getInfo?role=debuggingPartner&name=sarah&email=sarah@gmail.com");
    Assertions.assertEquals(200, getConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(getConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "No debuggingPartner found named sarah with email sarah@gmail.com",
        body.get("error_message"));
    getConnection.disconnect();
  }

  /** Test GetInfoHandler DebuggingPartnerInfoSuccessResponse */
  @Test
  public void testDebuggingPartnerInfoSuccessResponse() {
    DebuggingPartner claire = new DebuggingPartner("Claire", "claire@gmail.com");
    DebuggingPartnerInfoSuccessResponse response =
        new DebuggingPartnerInfoSuccessResponse(
            "success", "Sarah", "sarah@gmail.com", "bug", claire);
    Assertions.assertEquals("claire@gmail.com", response.email());
    Assertions.assertEquals("Claire", response.name());
    Assertions.assertEquals("bug", response.helpRequesterBug());
    Assertions.assertEquals("Sarah", response.helpRequesterName());
    Assertions.assertEquals("sarah@gmail.com", response.helpRequesterEmail());
    Assertions.assertEquals("success", response.message());
    Assertions.assertEquals("success", response.result());
    Assertions.assertEquals(false, response.flagged());
    Assertions.assertEquals(0, response.studentsHelped());
    Assertions.assertEquals(null, response.pairedAtTime());
    Assertions.assertEquals(3, response.joinedTime().split(":").length);
  }

  /** Test GetInfoHandler HelpRequesterInfoSuccessResponse */
  @Test
  public void testHelpRequesterInfoSuccessResponse() {
    HelpRequester claire = new HelpRequester("Claire", "claire@gmail.com", "bug");
    HelpRequesterInfoSuccessResponse response =
        new HelpRequesterInfoSuccessResponse("success", "Sarah", claire);
    Assertions.assertEquals("claire@gmail.com", response.email());
    Assertions.assertEquals("Claire", response.name());
    Assertions.assertEquals("bug", response.bugType());
    Assertions.assertEquals("Sarah", response.debuggingPartnerName());
    Assertions.assertEquals(false, response.debugged());
    Assertions.assertEquals(false, response.escalated());
    Assertions.assertEquals("success", response.message());
    Assertions.assertEquals("success", response.result());
    Assertions.assertEquals(null, response.pairedAtTime());
    Assertions.assertEquals(3, response.joinedTime().split(":").length);
  }
}
