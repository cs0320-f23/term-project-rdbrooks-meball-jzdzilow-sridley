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
 * This is a testing class that tests the AddHelpRequester class, which handles requests to the
 * /addHelpRequester endpoint.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestAddHelpRequesterHandler {
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
    SessionHandler sessionHandler =
        new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    AddHelpRequesterHandler addHelpRequesterHandler =
        new AddHelpRequesterHandler(helpRequesterQueue, sessionState);
    Spark.get("/session", sessionHandler);
    Spark.get("/addHelpRequester", addHelpRequesterHandler);
    Spark.awaitInitialization();
  }

  /** Resets the state after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/session");
    Spark.unmap("/addHelpRequester");
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
   * Tests adding a help requester with no session running
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testAddBeforeBegin() throws IOException {
    HttpURLConnection addConnection = tryRequest("addHelpRequester");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("No session is running.", body.get("error_message"));
    addConnection.disconnect();
  }

  /**
   * Tests adding a help requester with no parameters
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testAddNoParams() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection sessionConnection = tryRequest("addHelpRequester");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: name", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests adding a help requester with no email parameter
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testAddNoEmail() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection sessionConnection = tryRequest("addHelpRequester?name=Sarah");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: email", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests adding a help requester with no bug type parameter
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testAddNoBugType() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection sessionConnection = tryRequest("addHelpRequester?name=Sarah&email=gmail");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: bugType", body.get("error_message"));
    sessionConnection.disconnect();
  }

  /**
   * Tests adding a help requester successfully
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testAddSuccess() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection sessionConnection =
        tryRequest("addHelpRequester?name=Sarah&email=sr@gmai.com&bugType=bug");
    Assertions.assertEquals(200, sessionConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(sessionConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Help Requester Sarah was added to the queue.", body.get("message"));
    sessionConnection.disconnect();
  }
}
