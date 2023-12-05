package edu.brown.cs32.livecode.dispatcher.helpRequester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class is the HelpRequesterQueue class which contains HelpRequester objects in various lists.
 * The fields include a list of HelpRequesters that still need help, the HelpRequesters that are
 * currently getting helped, the HelpRequesters that have already been helped, and a combined list
 * of all HelpRequesters.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class HelpRequesterQueue {
  private final List<HelpRequester> needHelp;
  private final List<HelpRequester> gettingHelp = new ArrayList<>();

  private final List<HelpRequester> alreadyHelped = new ArrayList<>();
  private final List<HelpRequester> allHelpRequesters = new ArrayList<>();

  /**
   * Constructor for the HelpRequesterQueue class
   *
   * @param needHelp list of initial HelpRequesters that need help
   */
  public HelpRequesterQueue(List<HelpRequester> needHelp) {
    this.needHelp = needHelp;
  }

  /**
   * Clears all lists of HelpRequesters. Called once a session ends (after data has been written to
   * attendance CSVs).
   */
  public void reset() {
    needHelp.clear();
    gettingHelp.clear();
    alreadyHelped.clear();
    allHelpRequesters.clear();
  }

  /**
   * Getter that returns an iterator for all HelpRequesters that need help
   *
   * @return Iterator of HelpRequester representing all HelpRequesters that need help
   */
  public Iterator<HelpRequester> getNeedHelp() {
    return needHelp.iterator();
  }

  /**
   * Getter that returns an unmodifiable copy of all HelpRequesters that need help
   *
   * @return list of HelpRequesters that need help
   */
  public List<HelpRequester> getNeedHelpList() {
    return Collections.unmodifiableList(needHelp);
  }

  /**
   * Getter that returns an unmodifiable copy of all HelpRequesters that are getting help
   *
   * @return list of HelpRequesters getting help
   */
  public List<HelpRequester> getGettingHelpList() {
    return Collections.unmodifiableList(gettingHelp);
  }

  /**
   * Getter that returns an unmodifiable copy of all HelpRequesters that have already been helped
   * during this session
   *
   * @return list of HelpRequesters that have already been helped
   */
  public List<HelpRequester> getHelpedList() {
    return Collections.unmodifiableList(alreadyHelped);
  }

  /**
   * Getter that returns an unmodifiable copy of all HelpRequesters that have joined this session
   *
   * @return list of all HelpRequesters
   */
  public List<HelpRequester> getAllHelpRequesters() {
    return Collections.unmodifiableList(allHelpRequesters);
  }

  /**
   * Modifier that adds a new HelpRequester to the queue
   *
   * @param newHelpRequester HelpRequester that has joined
   */
  public void addNeedsHelp(HelpRequester newHelpRequester) {
    needHelp.add(newHelpRequester);
    allHelpRequesters.add(newHelpRequester);
  }

  /**
   * Modifier that claims a HelpRequester and moves them from needing help to getting help
   *
   * @param helpRequester HelpRequester that has been claimed by a DebuggingPartner
   */
  public void claimHelpRequester(HelpRequester helpRequester) {
    needHelp.remove(helpRequester);
    gettingHelp.add(helpRequester);
  }

  /**
   * Modifier that sets a HelpRequester as being done with being debugged
   *
   * @param name String name of the HelpRequester that is done
   * @return boolean representing whether the HelpRequester was found and removed
   */
  public boolean setDoneDebugging(String name) {
    boolean debugged = false;
    HelpRequester toRemove = null;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      if (name.equals(thisName)) {
        helpRequester.setDebugged(true);
        alreadyHelped.add(helpRequester);
        toRemove = helpRequester;
        debugged = true;
      }
    }
    gettingHelp.remove(toRemove);
    return debugged;
  }

  /**
   * Modifier that sets a HelpRequester as being escalated by name
   *
   * @param name String name of the HelpRequester to escalate
   * @return boolean representing whether the HelpRequester was successfully set as escalated
   */
  public boolean setEscalated(String name) {
    boolean escalated = false;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      if (name.equals(thisName)) {
        helpRequester.setEscalated();
        escalated = true;
      }
    }
    return escalated;
  }

  /**
   * Modifier that moves a HelpRequester back to the queue needing help if their assigned
   * DebuggingPartner was flagged
   *
   * @param name String name representing the name of the HelpRequester
   * @return boolean representing whether the HelpRequester was moved back to the queue
   */
  public boolean moveBackToQueue(String name) {
    boolean moved = false;
    HelpRequester toMove = null;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      if (name.equals(thisName)) {
        helpRequester.setDebuggingPartner(null);
        toMove = helpRequester;
        moved = true;
      }
    }
    if (toMove != null) {
      gettingHelp.remove(toMove);
      needHelp.add(toMove);
    }
    return moved;
  }
}
