package edu.brown.cs32.livecode.dispatcher.utils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This class is the Utils class which provides utility methods for dealing with date and time info.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class Utils {

  private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm:ss:SSS");

  /** Unused constructor */
  private Utils() {}

  /**
   * Function that returns a formatted, verbose representation of the current time
   *
   * @return String representing verbose current time
   */
  public static String timestamp() {
    return fmt.format(LocalTime.now());
  }

  /**
   * Function that returns a formatted, verbose representation of the current time and date
   *
   * @return String representing time and date
   */
  public static String date() {
    Date today = new Date();
    Long now = today.getTime();
    String dateTimeFormatted = new SimpleDateFormat("MM-dd-yyyy-hh-mm").format(now);
    return dateTimeFormatted;
  }

  /**
   * Function that returns a formatted, brief version of the current time
   *
   * @return String representing time
   */
  public static String simpleTime() {
    Date today = new Date();
    Long now = today.getTime();
    String dateTimeFormatted = new SimpleDateFormat("hh:mm:ss").format(now);
    return dateTimeFormatted;
  }
}
