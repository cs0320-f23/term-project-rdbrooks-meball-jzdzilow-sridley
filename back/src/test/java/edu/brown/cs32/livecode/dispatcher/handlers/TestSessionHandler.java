package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
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
 * This is a testing class that tests the SessionHandler class, which handles requests to the
 * /session endpoint to begin and end sessions.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestSessionHandler {
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /** Creates the state before each test */
  @BeforeEach
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    SessionState sessionState = new SessionState(false);
    SessionHandler handler =
        new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    Spark.get("/session", handler);
    Spark.awaitInitialization();
  }

  /** Resets the state after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/session");
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
   * Tests a session request with no parameters
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testRequestNoParams() throws IOException {
    HttpURLConnection sessionConnection = tryRequest("session");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: command", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests a session request with a misspelled parameter
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testRequestMisspelled() throws IOException {
    HttpURLConnection sessionConnection = tryRequest("session?command=begi");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Required parameter command must be end or begin.", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests an end session request even before beginning a session
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testRequestEndRequest() throws IOException {
    HttpURLConnection sessionConnection = tryRequest("session?command=end");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Cannot end if no session is running.", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests a begin session request then another begin session request
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testRequestBeginBegin() throws IOException {
    HttpURLConnection sessionConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    sessionConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Cannot begin if session is already running.", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests a begin session request then an end session request
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testRequestBeginEnd() throws IOException {
    HttpURLConnection sessionConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    sessionConnection = tryRequest("session?command=end");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Ended session!", body.get("message"));
    sessionConnection.disconnect();
  }
}
