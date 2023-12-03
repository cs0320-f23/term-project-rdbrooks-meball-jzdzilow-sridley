package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

public class HelpRequester {
  private final String name;
  private final String email;
  private final String joinedTime;
  private String pairedAtTime;
  private boolean debugged = false;
  private boolean escalated = false;
  private DebuggingPartner debuggingPartner = null;

  public HelpRequester(String name, String email) {
    this.name = name;
    this.email = email;
    this.joinedTime = Utils.simpleTime();
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }
  public String getJoinedTime() { return joinedTime; }

  public String getPairedAtTime() { return pairedAtTime; }

  public void setDebugged(boolean newDebugged) {
    this.debugged = newDebugged;
  }

  public boolean getDebugged() {
    return this.debugged;
  }

  public void setDebuggingPartner(DebuggingPartner debuggingPartnerHelper) {
    this.debuggingPartner = debuggingPartnerHelper;
    this.pairedAtTime = Utils.simpleTime();
  }

  public DebuggingPartner getDebuggingPartner() {
    return debuggingPartner;
  }

  public void setEscalated() {
    this.escalated = true;
  }

  public boolean getEscalated() {
    return this.escalated;
  }

  @Override
  public String toString() {
    return name;
  }
}
