package edu.brown.cs32.livecode.dispatcher.sessionState;

public class SessionState {
  private boolean running;
  private String beginTime;
  private String endTime;
  public SessionState(boolean running) {
    this.running = running;
  }
  public void setRunning(boolean newRunning) {
    running = newRunning;
  }

  public boolean getRunning() {
    return running;
  }

  public void setBeginTime(String newDateTime) {
    beginTime = newDateTime;
  }

  public String getBeginTime() {
    return beginTime;
  }

  public void setEndTime(String newDateTime) {
    endTime = newDateTime;
  }

  public String getEndTime() {
    return endTime;
  }
}
