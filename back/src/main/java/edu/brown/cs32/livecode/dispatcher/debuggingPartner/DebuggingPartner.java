package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

/**
 * A edu.brown.cs32.livecode.threads.TA is now a _runnable_, which means we can create a thread for
 * each.
 *
 * <p>Question: what would you do if you needed to create a thread for a class that didn't implement
 * Runnable? (Hint: consider what you can do with a proxy.)
 */
public class DebuggingPartner implements Runnable {
  private final String name;
  private int studentsHelped;
  private HelpRequester helping = null;

  public DebuggingPartner(String name) {
    this.name = name;
    this.studentsHelped = 0;
  }

  public String getName() {
    return name;
  }

  public void incrementStudentsHelper() {
    this.studentsHelped += 1;
  }

  public int getStudentsHelped() {
    return this.studentsHelped;
  }

  public HelpRequester getCurrentHelpRequester() {
    return helping;
  }

  public void seeStudent(HelpRequester helpRequester) throws Exception {
    if (this.helping != null) throw new Exception();
    this.helping = helpRequester;

    // *** Only uncomment one of these at a time ***

    // Version that won't allow multiple TAs to see students in parallel
    // run();

    // Version that *WILL* allow multiple TAs to see students in parallel
    // (This is what requires the Runnable interface)
    new Thread(this).start();
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isFree() {
    return helping == null;
  }

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
