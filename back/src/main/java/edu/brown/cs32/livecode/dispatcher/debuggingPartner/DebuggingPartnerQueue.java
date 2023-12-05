package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import java.util.ArrayList;
import java.util.List;

public class DebuggingPartnerQueue {
  private List<DebuggingPartner> debuggingPartnerList;
  private int index;
  private List<DebuggingPartner> allDebuggingPartners;

  public DebuggingPartnerQueue(List<DebuggingPartner> DebuggingPartnerList) {
    this.debuggingPartnerList = DebuggingPartnerList;
    this.index = 0;
    this.allDebuggingPartners = new ArrayList<>();
  }

  public void reset() {
    debuggingPartnerList.clear();
    index = 0;
  }

  public void addDebuggingPartner(DebuggingPartner newDebuggingPartner) {
    allDebuggingPartners.add(newDebuggingPartner);
    debuggingPartnerList.add(newDebuggingPartner);
  }

  public boolean removeDebuggingPartner(String name, String email) {
    boolean foundDebuggingPartner = false;
    List<DebuggingPartner> newDebuggingPartners = new ArrayList<>();
    for (DebuggingPartner debuggingPartner : debuggingPartnerList) {
      if (!debuggingPartner.getName().equals(name) || !debuggingPartner.getEmail().equals(email)) {
        newDebuggingPartners.add(debuggingPartner);
      } else {
        foundDebuggingPartner = true;
      }
    }
    debuggingPartnerList = newDebuggingPartners;
    return foundDebuggingPartner;
  }

  public List<DebuggingPartner> getDebuggingPartnerList() {
    return debuggingPartnerList;
  }

  public List<DebuggingPartner> getAllDebuggingPartnerList() {
    return allDebuggingPartners;
  }

  public boolean removeAndFlagDebuggingPartner(String name, String email) {
    boolean removed = false;
    DebuggingPartner toRemove = null;
    for (DebuggingPartner debuggingPartner : debuggingPartnerList) {
      String thisName = debuggingPartner.getName();
      String thisEmail = debuggingPartner.getEmail();
      if (name.equals(thisName) && email.equals(thisEmail)) {
        toRemove = debuggingPartner;
        removed = true;
        toRemove.setFlagged();
      }
    }
    if (toRemove != null) {
      debuggingPartnerList.remove(toRemove);
    }
    return removed;
  }

  public DebuggingPartner nextDebuggingPartner() {
    if (debuggingPartnerList.isEmpty()) {
      return null;
    } else if (index >= debuggingPartnerList.size()) {
      index = 1;
      return debuggingPartnerList.get(0);
    } else {
      index += 1;
      return debuggingPartnerList.get(index - 1);
    }
  }
}
