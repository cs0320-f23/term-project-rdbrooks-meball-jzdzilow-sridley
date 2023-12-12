package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
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
 * This is a testing class that tests the SubmitDebuggingQuestionsHandler class, which handles
 * requests to the /submitDebuggingQuestions endpoint.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestSubmitDebuggingQuestionsHandler {
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeEach
  public void setup() {
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    SessionState sessionState =
        new SessionState(
            false,
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-debugging-process-answers.csv");
    sessionState.setBeginTime(Utils.simpleTime());
    SessionHandler sessionHandler =
        new SessionHandler(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    SubmitDebuggingQuestionsHandler submitDebuggingQuestionsHandler =
        new SubmitDebuggingQuestionsHandler(sessionState);
    Spark.get("/session", sessionHandler);
    Spark.get("/submitDebuggingQuestions", submitDebuggingQuestionsHandler);
    Spark.awaitInitialization();
  }

  /** Resets the state after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/session");
    Spark.unmap("/submitDebuggingQuestions");
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

  /** Test when no session is running */
  @Test
  public void testNoSession() throws IOException {
    HttpURLConnection addConnection = tryRequest("submitDebuggingQuestions");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("No session is running.", body.get("error_message"));
    addConnection.disconnect();
  }

  /** Test when no parameters */
  @Test
  public void testNoParams() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection = tryRequest("submitDebuggingQuestions");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Missing required parameter: helpRequesterName", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test when only name and not email */
  @Test
  public void testNoEmail() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest("submitDebuggingQuestions?helpRequesterName=sarah");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Missing required parameter: helpRequesterEmail", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test when only name and email but no debugging partner */
  @Test
  public void testNoDebuggingName() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest(
            "submitDebuggingQuestions?helpRequesterName=sarah&helpRequesterEmail=sarah@gmail.com");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Missing required parameter: debuggingPartnerName", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test with everything supplied but debugging email */
  @Test
  public void testNoDebuggingZEmail() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest(
            "submitDebuggingQuestions?helpRequesterName=sarah&helpRequesterEmail=sarah@gmail.com&debuggingPartnerName=sarah");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Missing required parameter: debuggingPartnerEmail", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test with no bug category */
  @Test
  public void testNoCategory() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest(
            "submitDebuggingQuestions?helpRequesterName=sarah&helpRequesterEmail=sarah@gmail.com&debuggingPartnerName=sarah&debuggingPartnerEmail=sarah@gmail.com");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: bugCategory", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test with everything except debugging process */
  @Test
  public void testNoDebuggingProcess() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest(
            "submitDebuggingQuestions?helpRequesterName=sarah&helpRequesterEmail=sarah@gmail.com&debuggingPartnerName=sarah&debuggingPartnerEmail=sarah@gmail.com&bugCategory=bug");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Missing required parameter: debuggingProcess", body.get("error_message"));
    submitConnection.disconnect();
  }

  /** Test with everything */
  @Test
  public void testEverything() throws IOException {
    HttpURLConnection addConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection submitConnection =
        tryRequest(
            "submitDebuggingQuestions?helpRequesterName=sarah&helpRequesterEmail=sarah@gmail.com&debuggingPartnerName=claire&debuggingPartnerEmail=claire@gmail.com&bugCategory=bug&debuggingProcess=rah");
    Assertions.assertEquals(200, submitConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(submitConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals(
        "Debugging questions recorded for Debugging Partner claire and Help Requester sarah",
        body.get("message"));
    submitConnection.disconnect();
  }
}
