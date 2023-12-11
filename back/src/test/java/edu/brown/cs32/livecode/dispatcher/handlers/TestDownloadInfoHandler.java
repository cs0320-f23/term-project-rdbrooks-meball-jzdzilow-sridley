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
 * This is a testing class that tests the DownloadInfoHAndler class, which handles requests to the
 * /info endpoint.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestDownloadInfoHandler {
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
    DownloadInfoHandler downloadInfoHandler = new DownloadInfoHandler(sessionState);
    Spark.get("/session", sessionHandler);
    Spark.get("/info", downloadInfoHandler);
    Spark.awaitInitialization();
  }

  /** Resets the state after each test */
  @AfterEach
  public void tearDown() {
    Spark.unmap("/session");
    Spark.unmap("/info");
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
   * Testing with no parameters
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testNoParams() throws IOException {
    HttpURLConnection addConnection = tryRequest("info");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals("Missing required parameter: type", body.get("error_message"));
    addConnection.disconnect();
  }

  /**
   * Testing download none of the options
   *
   * @throws IOException if the request fails
   */
  @Test
  public void testNone() throws IOException {
    HttpURLConnection addConnection = tryRequest("info?type=aaaa");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("error_bad_request", body.get("result"));
    Assertions.assertEquals(
        "Please enter what type of info you would like to download", body.get("error_message"));
    addConnection.disconnect();
  }
}
