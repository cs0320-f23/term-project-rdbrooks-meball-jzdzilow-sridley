package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

/**
 * This class is the DebuggingPartner class, which is a classification of student that helps out
 * HelpRequesters. DebuggingPartners are stored in a DebuggingPartnerQueue.
 *
 * <p>A DebuggingPartner is a _runnable_ object, which means we can create a thread for each! This
 * thread will handle helping a HelpRequester.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class DebuggingPartner implements Runnable {
  private final String name;
  private final String email;
  private final String joinedTime;
  private String pairedAtTime;
  private int studentsHelped = 0;
  private HelpRequester helping = null;
  private boolean flagged = false;

  /**
   * Constructor for DebuggingPartner class
   *
   * @param name String representing the name of the DebuggingPartner
   * @param email String representing the email of the DebuggingPartner
   */
  public DebuggingPartner(String name, String email) {
    this.name = name;
    this.email = email;
    this.joinedTime = Utils.simpleTime();
  }

  /**
   * Getter for the name field
   *
   * @return String representing the DebuggingPartner's name
   */
  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getJoinedTime() {
    return joinedTime;
  }

  public String getPairedAtTime() {
    return pairedAtTime;
  }

  /** Updater to increment the DebuggingPartner's students helped */
  public void incrementStudentsHelper() {
    this.studentsHelped += 1;
  }

  /**
   * Getter for the studentsHelped field
   *
   * @return int representing the DebugginerPartner's studentsHelped
   */
  public int getStudentsHelped() {
    return this.studentsHelped;
  }

  /** Setter to turn flagged field to true */
  public void setFlagged() {
    this.flagged = true;
  }

  /**
   * Getter for the flagged field
   *
   * @return boolean representing the DebuggingPartner's flagged field
   */
  public boolean getFlagged() {
    return this.flagged;
  }

  /**
   * Getter for the helping field
   *
   * @return HelpRequester representing the DebuggingPartner's helping field
   */
  public HelpRequester getCurrentHelpRequester() {
    return helping;
  }

  /**
   * Creates a new thread to allow this DebuggingPartner to see a HelpRequester
   *
   * @param helpRequester the HelpRequester to see
   * @throws Exception if the DebuggingPartner is already helping someone
   */
  public void seeStudent(HelpRequester helpRequester) throws Exception {
    if (this.helping != null) throw new Exception();
    this.pairedAtTime = Utils.simpleTime();
    this.helping = helpRequester;
    new Thread(this).start();
  }

  /**
   * Overriding the toString function
   *
   * @return String containing the DebuggingPartner's name
   */
  @Override
  public String toString() {
    return name;
  }

  /**
   * Checks whether a DebuggingPartner is currently helping anyone
   *
   * @return boolean representing the DebuggingPartner's helping is null
   */
  public boolean isFree() {
    return helping == null;
  }

  /** Overriding the run function. Allows the DebuggingPartner to help a HelpRequester. */
  @Override
  public void run() {
    System.out.println(Utils.timestamp() + " " + name + " says: Hello " + helping + "!");
    while (!helping.getDebugged()) {
      try {
        // Help the student, however long they need
        Thread.sleep(500);
        // Thread.sleep(helping.getProblemTimeMilliseconds());
      } catch (InterruptedException e) {
        // Actually shouldn't happen...
        System.out.println(this.name + " was interrupted; had to stop helping.");
      }
    }

    System.out.println(
        Utils.timestamp() + " " + name + " says: Goodbye " + helping + ", I hope that helped!!");
    helping = null;
  }
}
