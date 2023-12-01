package edu.brown.cs32.livecode.dispatcher.utils;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {
  private Utils() {} // no constructor

  private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm:ss:SSS");

  public static String timestamp() {
    return fmt.format(LocalTime.now());
  }

  public static String date() {
    Date today = new Date();
    Long now = today.getTime();
    String dateTimeFormatted = new SimpleDateFormat("MM-dd-yyyy-hh-mm").format(now);
    return dateTimeFormatted;
  }
}
