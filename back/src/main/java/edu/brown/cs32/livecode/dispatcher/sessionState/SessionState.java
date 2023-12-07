package edu.brown.cs32.livecode.dispatcher.sessionState;

/**
 * This class is the SessionState class that handles the metadata for this session.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class SessionState {
  private boolean running;
  private String beginTime;
  private String endTime;
  private String answersFileName;

  /**
   * Constructor for the SessionState class
   *
   * @param running boolean representing value of running field
   */
  public SessionState(boolean running) {
    this.running = running;
    this.answersFileName = "data/debugging-process-answers.csv";
  }

  /**
   * Constructor for the SessionState class
   *
   * @param running boolean representing value of running field
   * @param answersFileName String representing name of file to write answers to (if not default)
   */
  public SessionState(boolean running, String answersFileName) {
    this.running = running;
    this.answersFileName = answersFileName;
  }

  /**
   * Setter for the running field
   *
   * @param newRunning boolean representing new value for the running field
   */
  public void setRunning(boolean newRunning) {
    running = newRunning;
  }

  /**
   * Getter for the running field
   *
   * @return boolean representing the running field
   */
  public boolean getRunning() {
    return running;
  }

  /**
   * Setter for the beginTime field
   *
   * @param newDateTime representing new value for the beginTime field
   */
  public void setBeginTime(String newDateTime) {
    beginTime = newDateTime;
  }

  /**
   * Getter for the beginTime field
   *
   * @return String representing the beginTime field
   */
  public String getBeginTime() {
    return beginTime;
  }

  /**
   * Setter for the endTime field
   *
   * @param newDateTime String representing new value for the endTime field
   */
  public void setEndTime(String newDateTime) {
    endTime = newDateTime;
  }

  /**
   * Getter for the endTime field
   *
   * @return String representing the endTime field
   */
  public String getEndTime() {
    return endTime;
  }

  /**
   * Getter for the answersFileName
   *
   * @return String representing the answersFileName field
   */
  public String getAnswersFileName() {
    return answersFileName;
  }
}
