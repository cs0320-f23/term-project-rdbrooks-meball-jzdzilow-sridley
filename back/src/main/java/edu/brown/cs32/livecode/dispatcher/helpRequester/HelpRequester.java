package edu.brown.cs32.livecode.dispatcher.helpRequester;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;

/**
 * This class is the HelpRequester class which represents a student that is requesting help.
 * HelpRequesters are stored in the HelpRequesterQueue.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class HelpRequester {
  private final String name;
  private final String email;
  private final String bugType;
  private final String joinedTime;
  private String pairedAtTime;
  private boolean debugged = false;
  private boolean escalated = false;
  private DebuggingPartner debuggingPartner = null;

  /**
   * Constructor for the HelpRequester class
   *
   * @param name String representing the name of the HelpRequester
   * @param email String representing the email of the HelpRequester
   */
  public HelpRequester(String name, String email, String bugType) {
    this.name = name;
    this.email = email;
    this.bugType = bugType;
    this.joinedTime = Utils.simpleTime();
  }

  /**
   * Getter for the HelpRequester's name field
   *
   * @return String representing the DebuggingPartner's name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for the HelpRequester's debugged field
   *
   * @param newDebugged boolean representing whether this HelpRequester has been debugged
   */
  public void setDebugged(boolean newDebugged) {
    this.debugged = newDebugged;
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

  public String getBugType() {
    return bugType;
  }

  /**
   * Getter for the HelpRequester's debugged field
   *
   * @return voolean representing whether this HelpRequester has been debugged
   */
  public boolean getDebugged() {
    return this.debugged;
  }

  /**
   * Setter for the HelpRequester's debuggingPartner field
   *
   * @param debuggingPartnerHelper DebuggingPartner that is helping the HelpRequester
   */
  public void setDebuggingPartner(DebuggingPartner debuggingPartnerHelper) {
    this.debuggingPartner = debuggingPartnerHelper;
    this.pairedAtTime = Utils.simpleTime();
  }

  /**
   * Getter for the HelpRequester's debuggingPartner field
   *
   * @return DebuggingPartner that is helping the HelpRequester
   */
  public DebuggingPartner getDebuggingPartner() {
    return debuggingPartner;
  }

  /** Setter that sets the HelpRequester's escalated field to true */
  public void setEscalated() {
    this.escalated = true;
  }

  /**
   * Getter for the HelpRequester's escalated field
   *
   * @return boolean representing whether the HelpRequester has been escalated
   */
  public boolean getEscalated() {
    return this.escalated;
  }

  /**
   * Overriding the toString method
   *
   * @return String containing the name of the HelpRequester
   */
  @Override
  public String toString() {
    return name;
  }
}
