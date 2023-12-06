package edu.brown.cs32.livecode.dispatcher.sessionState;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the SessionState class, which has represents the metadata of
 * sessions of Collab section.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestSessionState {
  /** Tests creation and default state of a Session State */
  @Test
  public void createSession() {
    SessionState sessionState = new SessionState(false);
    String beginTime = sessionState.getBeginTime();
    String endTime = sessionState.getEndTime();
    Assertions.assertEquals(sessionState.getRunning(), false);
    Assertions.assertEquals(beginTime, null);
    Assertions.assertEquals(endTime, null);
  }

  /** Tests creation, beginning, and ending of a Session State */
  @Test
  public void createBeginEndSession() {
    SessionState sessionState = new SessionState(false);
    Assertions.assertEquals(sessionState.getRunning(), false);
    Assertions.assertEquals(sessionState.getBeginTime(), null);
    Assertions.assertEquals(sessionState.getEndTime(), null);
    sessionState.setRunning(true);
    sessionState.setBeginTime("12:30");
    String beginTime = sessionState.getBeginTime();
    Assertions.assertEquals(sessionState.getRunning(), true);
    Assertions.assertEquals(beginTime, "12:30");
    Assertions.assertEquals(sessionState.getEndTime(), null);
    sessionState.setRunning(false);
    sessionState.setEndTime("2:30");
    String endTime = sessionState.getEndTime();
    Assertions.assertEquals(sessionState.getRunning(), false);
    Assertions.assertEquals(beginTime, "12:30");
    Assertions.assertEquals(endTime, "2:30");
  }
}
