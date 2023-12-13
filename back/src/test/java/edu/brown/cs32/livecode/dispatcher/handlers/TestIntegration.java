package edu.brown.cs32.livecode.dispatcher.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.server.HoursDispatcher;
import edu.brown.cs32.livecode.dispatcher.server.Server;
import edu.brown.cs32.livecode.dispatcher.sessionState.SessionState;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.Assertions;
import spark.Spark;

public class TestIntegration implements Runnable {

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private Moshi moshi = new Moshi.Builder().build();
  private JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

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

  private HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
  private DebuggingPartnerQueue debuggingPartnerQueue =
      new DebuggingPartnerQueue(new ArrayList<>());
  private HoursDispatcher dispatcher =
      new HoursDispatcher(helpRequesterQueue, debuggingPartnerQueue, "Concurrency");
  private SessionState sessionState = new SessionState(true);

  // @Test
  public void testIntegration() throws IOException, InterruptedException {
    Server server = new Server(helpRequesterQueue, debuggingPartnerQueue);
    new Thread(this).start();
    HttpURLConnection beginConnection = tryRequest("session?command=begin");
    Assertions.assertEquals(200, beginConnection.getResponseCode());
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(beginConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Began new session!", body.get("message"));
    HttpURLConnection addConnection =
        tryRequest("addHelpRequester?name=Sarah&email=sarah@gmail.com&bugType=bug");
    Assertions.assertEquals(200, addConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(addConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Help Requester Sarah was added to the queue.", body.get("message"));
    HttpURLConnection addConnectionAgain =
        tryRequest("addDebuggingPartner?name=Claire&email=claire@gmail.com");
    Assertions.assertEquals(200, addConnectionAgain.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(addConnectionAgain.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals(
        "Debugging Partner Claire was added to the queue.", body.get("message"));
    HttpURLConnection addConnectionThird =
        tryRequest("addDebuggingPartner?name=Sally&email=sally@gmail.com");
    Assertions.assertEquals(200, addConnectionThird.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(addConnectionThird.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals("Debugging Partner Sally was added to the queue.", body.get("message"));
    Thread.sleep(4000);
    HttpURLConnection getInfoConnection = tryRequest("getInfo");
    Assertions.assertEquals(200, getInfoConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(getInfoConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    String pairs = body.get("pairs").toString();
    Assertions.assertEquals(
        "[[[Claire, claire@gmail.com], [Sarah, sarah@gmail.com], [", pairs.substring(0, 57));
    HttpURLConnection flagAndRematchConnection =
        tryRequest(
            "flagAndRematch?helpRequesterName=Sarah&helpRequesterEmail=sarah@gmail.com&debuggingPartnerName=Claire&debuggingPartnerEmail=claire@gmail.com");
    Assertions.assertEquals(200, flagAndRematchConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(flagAndRematchConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    Assertions.assertEquals(
        "Debugging Partner Claire has been flagged, and Help Requester Sarah has been moved back to the queue.",
        body.get("message"));
    Thread.sleep(4000);
    getInfoConnection = tryRequest("getInfo");
    Assertions.assertEquals(200, getInfoConnection.getResponseCode());
    body = adapter.fromJson(new Buffer().readFrom(getInfoConnection.getInputStream()));
    Assertions.assertEquals("success", body.get("result"));
    pairs = body.get("pairs").toString();
    Assertions.assertEquals(
        "[[[Sally, sally@gmail.com], [Sarah, sarah@gmail.com], [", pairs.substring(0, 55));
  }

  @Override
  public void run() {
    while (true) {
      if (sessionState.getRunning()) {
        dispatcher.dispatch(sessionState, false);
      }
      System.out.print("");
    }
  }
}
