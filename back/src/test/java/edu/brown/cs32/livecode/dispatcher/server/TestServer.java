package edu.brown.cs32.livecode.dispatcher.server;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the Server class, which sets up endpoints and handles
 * requests.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestServer {

  /** Test creation of a server */
  @Test
  public void testServer() {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    Server server = new Server(helpRequesterQueue, debuggingPartnerQueue);
    Assertions.assertEquals(server.getClass(), Server.class);
  }

  /** Test parse CSV Instructors */
  @Test
  public void testParseCsvInstructorsEmpty() {
    List<List<String>> instructors = Server.parseCsvInstructors();
    Assertions.assertEquals(List.of("Name", "Email"), instructors.get(0));
  }
}
