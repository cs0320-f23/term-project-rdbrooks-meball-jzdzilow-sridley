package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;

public class HelpRequester {
  private final String name;
  private boolean debugged;
  private DebuggingPartner debuggingPartner;

  public HelpRequester(String name) {
    this.name = name;
    this.debugged = false;
    this.debuggingPartner = null;
  }

  public String getName() {
    return name;
  }

  public void setDebugged(boolean newDebugged) {
    this.debugged = newDebugged;
  }

  public boolean getDebugged() {
    return this.debugged;
  }

  public void setDebuggingPartner(DebuggingPartner debuggingPartnerHelper) {
    this.debuggingPartner = debuggingPartnerHelper;
  }

  public DebuggingPartner getDebuggingPartner() {
    return debuggingPartner;
  }

  @Override
  public String toString() {
    return name;
  }
}
