package edu.brown.cs32.livecode.dispatcher.sessionState;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import edu.brown.cs32.livecode.dispatcher.utils.Utils;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvWriter {
  private final HelpRequesterQueue helpRequesterQueue;
  private final DebuggingPartnerQueue debuggingPartnerQueue;
  private final SessionState sessionState;

  private String dateTime;

  public CsvWriter(
      HelpRequesterQueue helpRequesterQueue,
      DebuggingPartnerQueue debuggingPartnerQueue,
      SessionState sessionState) {
    this.helpRequesterQueue = helpRequesterQueue;
    this.debuggingPartnerQueue = debuggingPartnerQueue;
    this.sessionState = sessionState;
    this.dateTime = Utils.date();
  }

  public void writeAllAttendance() {
    try {
      FileWriter myWriter = new FileWriter("data/all-attendance.csv", true);
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

  public void writeSessionAttendance(String dateTime) {
    try {
      FileWriter dpWriter =
          new FileWriter("data/sessions/debugging-partner-attendance-" + dateTime + ".csv", true);
      FileWriter hrWriter =
          new FileWriter("data/sessions/help-requester-attendance-" + dateTime + ".csv", true);
      dpWriter.write("DebuggingPartnerName, HelpRequestersSeen\n");
      hrWriter.write("HelpRequesterName, AssignedDebuggingPartnerName\n");

      List<HelpRequester> allHelpRequesters = helpRequesterQueue.getAllHelpRequesters();
      List<DebuggingPartner> allDebuggingPartners =
          debuggingPartnerQueue.getAllDebuggingPartnerList();
      for (DebuggingPartner debuggingPartner : allDebuggingPartners) {
        dpWriter.write(
            debuggingPartner.getName() + ", " + debuggingPartner.getStudentsHelped() + "\n");
      }
      dpWriter.close();
      for (HelpRequester helpRequester : allHelpRequesters) {
        hrWriter.write(helpRequester.getName() + ", " + helpRequester.getDebuggingPartner() + "\n");
      }
      hrWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void write() {
    writeAllAttendance();
    writeSessionAttendance();
  }
}
