package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HelpRequesterQueue {
  private final List<HelpRequester> needHelp;
  private final List<HelpRequester> gettingHelp;

  private final List<HelpRequester> alreadyHelped;
  private final List<HelpRequester> allHelpRequesters;

  public HelpRequesterQueue(List<HelpRequester> needHelp) {
    this.needHelp = needHelp;
    this.gettingHelp = new ArrayList<>();
    this.alreadyHelped = new ArrayList<>();
    this.allHelpRequesters = new ArrayList<>();
  }

  public void reset() {
    needHelp.clear();
    gettingHelp.clear();
    alreadyHelped.clear();
  }

  public Iterator<HelpRequester> getNeedHelp() {
    return needHelp.iterator();
  }

  public List<HelpRequester> getNeedHelpList() {
    return needHelp;
  }

  public List<HelpRequester> getGettingHelpList() {
    return gettingHelp;
  }

  public List<HelpRequester> getHelpedList() {
    return alreadyHelped;
  }

  public List<HelpRequester> getAllHelpRequesters() {
    return allHelpRequesters;
  }

  public void addNeedsHelp(HelpRequester newHelpRequester) {
    needHelp.add(newHelpRequester);
    allHelpRequesters.add(newHelpRequester);
  }

  public void claimHelpRequester(HelpRequester helpRequester) {
    needHelp.remove(helpRequester);
    gettingHelp.add(helpRequester);
  }

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
    gettingHelp.remove(toRemove);
    return debugged;
  }

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
