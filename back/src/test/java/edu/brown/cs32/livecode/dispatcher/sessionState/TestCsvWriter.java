package edu.brown.cs32.livecode.dispatcher.sessionState;

import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartner;
import edu.brown.cs32.livecode.dispatcher.debuggingPartner.DebuggingPartnerQueue;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequester;
import edu.brown.cs32.livecode.dispatcher.helpRequester.HelpRequesterQueue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the CsvWriter class, which writes attendance information to
 * the data directory. In these tests, the output is written to the data directory within the
 * sessionState testing package.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestCsvWriter {

  /**
   * Reset the test-all-attendance.csv file
   *
   * @throws IOException if the file is not found
   */
  @BeforeAll
  public static void resetAllAttendance() throws IOException {
    FileWriter myWriter =
        new FileWriter(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-all-attendance.csv");
    myWriter.write("BeginTime, EndTime, AllDBPs, AllHRQs, Pairs, UnassignedDBPs, UnseenHRQs\n");
    myWriter.close();
  }

  /**
   * This tests writing all-attendance when no one shows up
   *
   * @throws Exception if file is not found
   */
  @Test
  public void writeAllAttendanceNone() throws Exception {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(List.of());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(List.of());
    SessionState sessionState = new SessionState(true);
    CsvWriter csvWriter = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    csvWriter.writeAllAttendance(
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-all-attendance.csv");
    FileReader fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-all-attendance.csv");
    List<String> allAttendanceData = parseCsv(new BufferedReader(fileReader));
    String lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals("null, null, , , , , ", lastRow);
  }

  /**
   * This test writes all-attendance when debugging partners and help requesters do show up
   *
   * @throws Exception if file is not found
   */
  @Test
  public void writeAllAttendanceWithStudents() throws Exception {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    helpRequesterQueue.addNeedsHelp(new HelpRequester("Sarah", "sarah_ridley@brown.edu", "bug"));
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    debuggingPartnerQueue.addDebuggingPartner(
        new DebuggingPartner("Megan", "megan_ball@brown.edu"));

    SessionState sessionState = new SessionState(true);
    CsvWriter csvWriter = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    csvWriter.writeAllAttendance(
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-all-attendance.csv");
    FileReader fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/test-all-attendance.csv");
    List<String> allAttendanceData = parseCsv(new BufferedReader(fileReader));
    String lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals("null, null, Megan: 0, Sarah, , Megan, Sarah", lastRow);
  }

  /**
   * This tests writes session attendance when no one joins
   *
   * @throws Exception if the file is not found
   */
  @Test
  public void writeSessionAttendanceNone() throws Exception {
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(List.of());
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(List.of());
    SessionState sessionState = new SessionState(true);
    CsvWriter csvWriter = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    csvWriter.writeSessionAttendance(
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/db-none.csv",
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/hr-none.csv");
    FileReader fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/db-none.csv");
    List<String> allAttendanceData = parseCsv(new BufferedReader(fileReader));
    String lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals(
        "DebuggingPartnerName, DebuggingPartnerEmail, JoinedTime, LastPairedAtTime, HelpRequestersSeen",
        lastRow);
    fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/hr-none.csv");
    allAttendanceData = parseCsv(new BufferedReader(fileReader));
    lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals(
        "HelpRequesterName, HelpRequesterEmail, JoinedTime, PairAtTime, AssignedDebuggingPartnerName",
        lastRow);
  }

  /**
   * This tests writes session attendance when students join
   *
   * @throws Exception if the file is not found
   */
  @Test
  public void writeSessionAttendanceWithStudents() throws Exception {
    HelpRequester sarah = new HelpRequester("Sarah", "sarah_ridley@brown.edu", "bug");
    DebuggingPartner megan = new DebuggingPartner("Megan", "megan_ball@brown.edu");
    HelpRequesterQueue helpRequesterQueue = new HelpRequesterQueue(new ArrayList<>());
    helpRequesterQueue.addNeedsHelp(sarah);
    DebuggingPartnerQueue debuggingPartnerQueue = new DebuggingPartnerQueue(new ArrayList<>());
    debuggingPartnerQueue.addDebuggingPartner(megan);

    SessionState sessionState = new SessionState(true);
    CsvWriter csvWriter = new CsvWriter(helpRequesterQueue, debuggingPartnerQueue, sessionState);
    csvWriter.writeSessionAttendance(
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/db-some.csv",
        "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/hr-some.csv");
    FileReader fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/db-some.csv");
    List<String> allAttendanceData = parseCsv(new BufferedReader(fileReader));
    String lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals(
        "Megan, megan_ball@brown.edu, " + megan.getJoinedTime() + ", null, 0", lastRow);
    fileReader =
        new FileReader(
            "src/test/java/edu/brown/cs32/livecode/dispatcher/sessionState/data/sessions/hr-some.csv");
    allAttendanceData = parseCsv(new BufferedReader(fileReader));
    lastRow = allAttendanceData.get(allAttendanceData.size() - 1);
    Assertions.assertEquals(
        "Sarah, sarah_ridley@brown.edu, " + sarah.getJoinedTime() + ", null, null", lastRow);
  }

  /**
   * Helper function to parse the CSVs where data has been entered
   *
   * @param buffReader BufferedReader to read CSV data from
   * @return list of String representing CSV data
   * @throws Exception if the file is not found
   */
  public List<String> parseCsv(BufferedReader buffReader) throws Exception {
    List<String> csvData = new ArrayList<>();
    String line = buffReader.readLine();
    while (line != null) {
      csvData.add(line);
      line = buffReader.readLine();
    }
    return csvData;
  }
}
