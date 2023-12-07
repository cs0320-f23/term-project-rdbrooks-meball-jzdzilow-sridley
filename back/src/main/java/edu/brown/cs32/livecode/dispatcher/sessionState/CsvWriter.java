package edu.brown.cs32.livecode.dispatcher.sessionState;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the CsvWriter class that handles writing attendance information to the data
 * directory. It writes to CSVs for session HelpRequester data, session DebuggingPartner data, and
 * contributes to a running all-attendance.csv file that keeps data from all sessions.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class CsvWriter {
  private final HelpRequesterQueue helpRequesterQueue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private final SessionState sessionState;

  /**
   * Constructor for the CSVWriter class
   *
   * @param helpRequesterQueue HelpRequesterQueue containing all HelpRequester info
   * @param debuggingPartnerQueue DebuggingPartnerQueue containing all DebuggingPartner info
   * @param sessionState SessionState representing the current state of the session
   */
  public CsvWriter(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
  }

  /** Helper function that writes to the all-attendance.csv file in the data directory */
  public void writeAllAttendance(String allAttendanceFileName) {
    try {
      FileWriter myWriter = new FileWriter(allAttendanceFileName, true);
      String beginTime = sessionState.getBeginTime();
      String endTime = sessionState.getEndTime();
      List<HelpRequester> allHelpRequesters = helpRequesterQueue.getAllHelpRequesters();
      List<String> allHelpRequestersNames = new ArrayList<>();
      List<String> unseenHelpRequestersNames = new ArrayList<>();
      List<DebuggingPartner> allDebuggingPartners =
          debuggingPartnerQueue.getAllDebuggingPartnerList();
      List<String> allDebuggingPartnersNames = new ArrayList<>();
      List<String> unseenDebuggingPartnersNames = new ArrayList<>();
      for (DebuggingPartner debuggingPartner : allDebuggingPartners) {
        allDebuggingPartnersNames.add(
            debuggingPartner.getName() + ": " + debuggingPartner.getStudentsHelped());
        if (debuggingPartner.getStudentsHelped() == 0) {
          unseenDebuggingPartnersNames.add(debuggingPartner.getName());
        }
      }
      List<String> pairs = new ArrayList<>();
      for (HelpRequester helpRequester : allHelpRequesters) {
        allHelpRequestersNames.add(helpRequester.getName());
        DebuggingPartner helper = helpRequester.getDebuggingPartner();
        if (helper != null) {
          pairs.add(
              "Help Requester: "
                  + helpRequester.getName()
                  + ", Debugging Partner: "
                  + helper.getName());
        } else {
          unseenHelpRequestersNames.add(helpRequester.getName());
        }
      }
      String toWrite = beginTime + ", " + endTime + ", ";
      toWrite += String.join("; ", allDebuggingPartnersNames) + ", ";
      toWrite += String.join("; ", allHelpRequestersNames) + ", ";
      toWrite += String.join("; ", pairs) + ", ";
      toWrite += String.join("; ", unseenDebuggingPartnersNames) + ", ";
      toWrite += String.join("; ", unseenHelpRequestersNames) + "\n";
      myWriter.write(toWrite);
      myWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Helper function that writes to a new HelpRequester file and a new DebuggingPartner file in the
   * sessions directory inside the data directory
   */
  public void writeSessionAttendance(
      String debuggingPartnerFileName, String helpRequesterFileName) {
    try {
      FileWriter dpWriter = new FileWriter(debuggingPartnerFileName, true);
      FileWriter hrWriter = new FileWriter(helpRequesterFileName, true);
      dpWriter.write(
          "DebuggingPartnerName, DebuggingPartnerEmail, JoinedTime, LastPairedAtTime, HelpRequestersSeen\n");
      hrWriter.write(
          "HelpRequesterName, HelpRequesterEmail, JoinedTime, PairAtTime, AssignedDebuggingPartnerName\n");

      List<HelpRequester> allHelpRequesters = helpRequesterQueue.getAllHelpRequesters();
      List<DebuggingPartner> allDebuggingPartners =
          debuggingPartnerQueue.getAllDebuggingPartnerList();
      for (DebuggingPartner debuggingPartner : allDebuggingPartners) {
        dpWriter.write(
            debuggingPartner.getName()
                + ", "
                + debuggingPartner.getEmail()
                + ", "
                + debuggingPartner.getJoinedTime()
                + ", "
                + debuggingPartner.getPairedAtTime()
                + ", "
                + debuggingPartner.getStudentsHelped()
                + "\n");
      }
      dpWriter.close();
      for (HelpRequester helpRequester : allHelpRequesters) {
        hrWriter.write(
            helpRequester.getName()
                + ", "
                + helpRequester.getEmail()
                + ", "
                + helpRequester.getJoinedTime()
                + ", "
                + helpRequester.getPairedAtTime()
                + ", "
                + helpRequester.getDebuggingPartner()
                + "\n");
      }
      hrWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Main function that calls the helper functions to write all attendance information */
  public void write() {
    String beginTime = sessionState.getBeginTime();
    writeAllAttendance("data/all-attendance.csv");
    writeSessionAttendance(
        "data/sessions/debugging-partner-attendance-" + beginTime + ".csv",
        "data/sessions/help-requester-attendance-" + beginTime + ".csv");
  }
}
