package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
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
  private List<HelpRequester> allHelpRequesters = new ArrayList<>();

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
   * @param email String email of the HelpRequester that is done
   * @return boolean representing whether the HelpRequester was found and removed
   */
  public boolean setDoneDebugging(String name, String email) {
    boolean debugged = false;
    HelpRequester toRemove = null;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      String thisEmail = helpRequester.getEmail();
      if (name.equals(thisName) && email.equals(thisEmail)) {
        helpRequester.setDebugged(true);
        alreadyHelped.add(helpRequester);
        toRemove = helpRequester;
        debugged = true;
      }
    }
    if (debugged) {
      gettingHelp.remove(toRemove);
      return true;

    } else {
      for (HelpRequester helpRequester : needHelp) {
        String thisName = helpRequester.getName();
        String thisEmail = helpRequester.getEmail();
        if (name.equals(thisName) && email.equals(thisEmail)) {
          helpRequester.setDebugged(true);
          toRemove = helpRequester;
          debugged = true;
        }
      }
      needHelp.remove(toRemove);
      return debugged;
    }
  }

  /**
   * Modifier that sets a HelpRequester as being escalated by name
   *
   * @param helpRequesterName String name of the HelpRequester to escalate
   * @param helpRequesterEmail String email of the HelpRequester to escalate
   * @return boolean representing whether the HelpRequester was successfully set as escalated
   */
  public boolean setEscalated(String helpRequesterName, String helpRequesterEmail) {
    boolean escalated = false;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      String thisEmail = helpRequester.getEmail();
      if (helpRequesterName.equals(thisName) && helpRequesterEmail.equals(thisEmail)) {
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
   * @param debuggingPartnerName String name representing the name of the DebuggingPartner
   * @param helpRequesterName String name representing the name of the HelpRequester
   * @return boolean representing whether the HelpRequester was moved back to the queue
   */
  public boolean checkPaired(String debuggingPartnerName, String helpRequesterName) {
    for (HelpRequester helpRequester : gettingHelp) {
      String thisHelpRequesterName = helpRequester.getName();
      if (thisHelpRequesterName.equals(helpRequesterName)) {
        DebuggingPartner debuggingPartner = helpRequester.getDebuggingPartner();
        if (debuggingPartner != null) {
          String thisDebuggingPartnerName = debuggingPartner.getName();
          if (thisDebuggingPartnerName.equals(debuggingPartnerName)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public void rematchByDebuggingPartner(String debuggingPartnerName, String debuggingPartnerEmail) {
    HelpRequester toMove = null;
    for (HelpRequester helpRequester : gettingHelp) {
      DebuggingPartner helper = helpRequester.getDebuggingPartner();
      if (helper != null
          && (helper.getName().equals(debuggingPartnerName))
          && helper.getEmail().equals(debuggingPartnerEmail)) {
        helpRequester.setDebuggingPartner(null);
        toMove = helpRequester;
      }
    }
    if (toMove != null) {
      gettingHelp.remove(toMove);
      needHelp.add(toMove);
    }
  }

  /**
   * Removes a DebuggingPartner from the list of all debugging partners, used for attendance.
   *
   * @param name String name of DebuggingPartner to remove from the queue
   * @param email String email of DebuggingPartner to remove from the queue
   */
  public void removeFromAttendanceList(String name, String email) {
    List<HelpRequester> newHelpRequesters = new ArrayList<>();
    for (HelpRequester helpRequester : allHelpRequesters) {
      if (!helpRequester.getName().equals(name) || !helpRequester.getEmail().equals(email)) {
        newHelpRequesters.add(helpRequester);
      } else {
        break;
      }
    }
    allHelpRequesters = newHelpRequesters;
  }

  public boolean moveBackToQueue(String name, String email) {
    boolean moved = false;
    HelpRequester toMove = null;
    for (HelpRequester helpRequester : gettingHelp) {
      String thisName = helpRequester.getName();
      String thisEmail = helpRequester.getEmail();
      if (name.equals(thisName) && email.equals(thisEmail)) {
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
