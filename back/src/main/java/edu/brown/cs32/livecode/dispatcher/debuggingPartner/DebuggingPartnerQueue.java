package edu.brown.cs32.livecode.dispatcher.debuggingPartner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is the DebuggingPartnerQueue class, which a structure that contains DebuggingPartners
 * a maintains the order that they are selected next to help students.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class DebuggingPartnerQueue {
  private List<DebuggingPartner> debuggingPartnerList;
  private int index = 0;
  private List<DebuggingPartner> allDebuggingPartners = new ArrayList<>();

  /**
   * Constructor for the DebuggingPartnerList class
   *
   * @param DebuggingPartnerList list of DebuggingPartners to start with
   */
  public DebuggingPartnerQueue(List<DebuggingPartner> DebuggingPartnerList) {
    this.debuggingPartnerList = DebuggingPartnerList;
  }

  /**
   * Clears the list of debugging partners and restores the index. Called once a session ends (after
   * data has been written to attendance CSVs).
   */
  public void reset() {
    debuggingPartnerList.clear();
    allDebuggingPartners.clear();
    index = 0;
  }

  /**
   * Adds a DebuggingPartner to the queue.
   *
   * @param newDebuggingPartner DebuggingPartner representing new student joining queue
   */
  public void addDebuggingPartner(DebuggingPartner newDebuggingPartner) {
    allDebuggingPartners.add(newDebuggingPartner);
    debuggingPartnerList.add(newDebuggingPartner);
  }

  /**
   * Removes a DebuggingPartner from the queue by name.
   *
   * @param name String name of DebuggingPartner to remove from the queue
   * @param email String email of DebuggingPartner to remove from the queue
   * @return boolean representing whether the DebuggingPartner was removed successfully
   */
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

  /**
   * Removes a DebuggingPartner from the list of all debugging partners, used for attendance.
   *
   * @param name String name of DebuggingPartner to remove from the queue
   * @param email String email of DebuggingPartner to remove from the queue
   */
  public boolean removeFromAttendanceList(String name, String email) {
    boolean found = false;
    List<DebuggingPartner> newDebuggingPartners = new ArrayList<>();
    for (DebuggingPartner debuggingPartner : allDebuggingPartners) {
      if (!debuggingPartner.getName().equals(name) || !debuggingPartner.getEmail().equals(email)) {
        newDebuggingPartners.add(debuggingPartner);
      } else {
        found = true;
      }
    }
    allDebuggingPartners = newDebuggingPartners;
    return found;
  }

  /**
   * Getter for an unmodifiable copy of the list of the current DebuggingPartners
   *
   * @return list of current DebuggingPartners
   */
  public List<DebuggingPartner> getDebuggingPartnerList() {
    return Collections.unmodifiableList(debuggingPartnerList);
  }

  /**
   * Getter for an unmodifiable copy of the list of all DebuggingPartners for this session
   *
   * @return list of all DebuggingPartners
   */
  public List<DebuggingPartner> getAllDebuggingPartnerList() {
    return Collections.unmodifiableList(allDebuggingPartners);
  }

  /**
   * Removes and flags a DebuggingPartner, given that they did not show up for the session but
   * joined the queue and were assigned to a student.
   *
   * @param name String representing name of DebuggingPartner to flag
   * @param email String representing email of DebuggingPartner to flag
   * @return boolean representing whether DebuggingPartner was successfully flagged and removed
   */
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

  /**
   * Gets the next DebuggingPartner in the queue and updates the index
   *
   * @return DebuggingPartner that is next in the queue
   */
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
