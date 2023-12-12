package edu.brown.cs32.livecode.dispatcher.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * This is a testing class that tests the Utils class, which has utility functions relating to time
 * and date.
 *
 * @author sarahridley juliazdzilowska rachelbrooks meganball
 * @version 1.0
 */
public class TestUtils {

  /** Testing the timeStamp utility function */
  @Test
  public void testTimeStamp() {
    String timeStamp = Utils.timestamp();
    String[] splitUtils = timeStamp.split(":");
    Assertions.assertEquals(splitUtils.length, 4);
  }

  /** Testing the date utility function */
  @Test
  public void testDate() {
    String date = Utils.date();
    String[] splitUtils = date.split("-");
    Assertions.assertEquals(splitUtils.length, 5);
  }

  /** Testing the date utility function */
  @Test
  public void testSimpleTime() {
    String simpleTime = Utils.simpleTime();
    String[] splitUtils = simpleTime.split(":");
    Assertions.assertEquals(splitUtils.length, 3);
  }
}
