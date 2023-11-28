package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
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

  public boolean removeDebuggingPartner(String name) {
    boolean foundDebuggingPartner = false;
    List<DebuggingPartner> newDebuggingPartners = new ArrayList<>();
    for (DebuggingPartner debuggingPartner : debuggingPartnerList) {
      if (!debuggingPartner.getName().equals(name)) {
        newDebuggingPartners.add(debuggingPartner);
      }
      else {
        foundDebuggingPartner = true;
      }
    }
    debuggingPartnerList = newDebuggingPartners;
    return foundDebuggingPartner;
  }

  public List<DebuggingPartner> getDebuggingPartnerList() {
    return debuggingPartnerList;
  }
  public List<DebuggingPartner> getAllDebuggingPartnerList() {return allDebuggingPartners;}

  public DebuggingPartner nextDebuggingPartner(){
    if (debuggingPartnerList.isEmpty()) {
      return null;
    }
    else if (index >= debuggingPartnerList.size()) {
      index = 1;
      return debuggingPartnerList.get(0);
    } else {
      index += 1;
      return debuggingPartnerList.get(index - 1);
    }
  }
}
