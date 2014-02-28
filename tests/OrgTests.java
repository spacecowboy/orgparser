package tests;

import org.cowboyprogrammer.org.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.joda.time.LocalDateTime;

@RunWith(JUnit4.class)
public class OrgTests {

  @Test
  public void thisAlwaysPasses() {

  }


  static void print(final int... numbers) {
    for (final int n: numbers) {
      System.out.println(n);
      System.out.print(" ");
    }
  }

  static void print(final Collection<String> strings) {
    for (final String s: strings) {
      System.out.print(s);
      System.out.print(" ");
    }
    System.out.println("");
  }

  static void print(final String... strings) {
    for (final String s: strings) {
      System.out.print(s);
      System.out.print(" ");
    }
    System.out.println("");
  }

  @Test
  @Ignore
  public void testfile() {
    final String fname = "test.org";

    try {

      final OrgFile root = OrgFile.createFrom(fname);
      print(root.treeToString());


      OrgNode leaf = root;
      while (leaf != null) {
        print(leaf.getAllTags());
        if (leaf.subNodes.isEmpty()) {
          leaf = null;
        }
        else {
          leaf = leaf.subNodes.get(0);
        }
      }

      root.writeToFile("test-out.org");

    } catch (FileNotFoundException e) {
      print(e.getMessage());
    } catch (IOException e) {
      print(e.getMessage());
    } catch (ParseException e) {
      print(e.getMessage());
    }
  }

  @Test
  public void testDefaultHeaderPattern() {
    Pattern p = OrgParser.getHeaderPattern();
    Matcher m = p.matcher("* BOB A simple title :bob:alice:");
    assertTrue(m.matches());
    assertEquals(m.group("stars"), "*");
    assertEquals(m.group("title"), "BOB A simple title");
    assertNull(m.group("todo"));
    assertEquals(m.group("tags"), ":bob:alice:");
  }

  @Test
  public void testHeaderPatternTODO() {
    Pattern p = OrgParser.getHeaderPattern("BOB");
    Matcher m = p.matcher("* BOB A simple title :bob:alice:");
    assertTrue(m.matches());
    assertEquals(m.group("stars"), "*");
    assertEquals(m.group("title"), "A simple title");
    assertEquals(m.group("todo"), "BOB");
    assertEquals(m.group("tags"), ":bob:alice:");
  }

  @Test
  public void testTimestampRangePattern() {
    Pattern p = OrgParser.getTimestampRangePattern();
    print(p.toString());
    Matcher m = p.matcher("<2013-12-31 Tue 12:21>--<2014-02-28 Wed 19:21>");
    assertTrue(m.matches());
    assertEquals(m.group("startdate"), "2013-12-31");
    assertEquals(m.group("startday"), "Tue");
    assertEquals(m.group("starttime"), "12:21");
    assertEquals(m.group("enddate"), "2014-02-28");
    assertEquals(m.group("endday"), "Wed");
    assertEquals(m.group("endtime"), "19:21");

    m = p.matcher("<2013-12-31 12:21>--<2014-02-28 19:21>");
    assertTrue(m.matches());
    assertEquals(m.group("startdate"), "2013-12-31");
    assertEquals(m.group("starttime"), "12:21");
    assertEquals(m.group("enddate"), "2014-02-28");
    assertEquals(m.group("endtime"), "19:21");

    m = p.matcher("<2013-12-31>--<2014-02-28>");
    assertTrue(m.matches());
    assertEquals(m.group("startdate"), "2013-12-31");
    assertEquals(m.group("enddate"), "2014-02-28");
  }


  @Test
  public void testTimestampPatternFull() {
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher("<2013-12-31 Tue 12:21-14:59 ++1w -2d>");
    assertTrue(m.matches());
    assertEquals(m.group("date"), "2013-12-31");
    assertEquals(m.group("day"), "Tue");
    assertEquals(m.group("time"), "12:21");
    assertEquals(m.group("timeend"), "14:59");
    assertEquals(m.group("repeat"), "++1w");
    assertEquals(m.group("warning"), "-2d");
  }

  @Test
  public void testTimestampPatternMinimum() {
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher("<2013-12-31>");
    assertTrue(m.matches());
    assertEquals(m.group("date"), "2013-12-31");
    assertNull(m.group("day"));
    assertNull(m.group("time"));
    assertNull(m.group("timeend"));
    assertNull(m.group("repeat"));
    assertNull(m.group("warning"));
  }

  @Test
  public void testTimestampPatternParts() {
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher("<2013-12-31 12:30 -1d>");
    assertTrue(m.matches());
    assertEquals(m.group("date"), "2013-12-31");
    assertNull(m.group("day"));
    assertEquals(m.group("time"), "12:30");
    assertNull(m.group("timeend"));
    assertNull(m.group("repeat"));
    assertEquals(m.group("warning"), "-1d");
  }

  @Test
  public void testTimestampPatternSpaces() {
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher("  <2013-12-31 12:30 -1d>  ");
    assertTrue(m.matches());
    assertEquals(m.group("date"), "2013-12-31");
    assertNull(m.group("day"));
    assertEquals(m.group("time"), "12:30");
    assertNull(m.group("timeend"));
    assertNull(m.group("repeat"));
    assertEquals(m.group("warning"), "-1d");
  }

  @Test
  public void testTimestampToString1() throws Exception {
    final String s = "<2013-12-31>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue>", res);
  }


  @Test
  public void testTimestampToString2() throws Exception {
    final String s = "<2013-12-31 12:30>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30>", res);
  }

  @Test
  public void testTimestampToString3() throws Exception {
    final String s = "<2013-12-31 12:30 -1w>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 -1w>", res);
  }

  @Test
  public void testTimestampToString4() throws Exception {
    final String s = "<2013-12-31 12:30 ++4y>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4y>", res);
  }
  @Test
  public void testTimestampPattern4a() throws Exception {
    final String s = "<2013-12-31 12:30 ++4m>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4m>", res);
  }
  @Test
  public void testTimestampPattern4b() throws Exception {
    final String s = "<2013-12-31 12:30 ++4w>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4w>", res);
  }
  @Test
  public void testTimestampPattern4c() throws Exception {
    final String s = "<2013-12-31 12:30 ++4d>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4d>", res);
  }
  @Test
  public void testTimestampPattern4d() throws Exception {
    final String s = "<2013-12-31 12:30 ++4h>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4h>", res);
  }


  @Test
  public void testTimestampToString5Dur() throws Exception {
    final String s = "<2013-12-31 12:30-19:12 ++4d>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30-19:12 ++4d>", res);
  }




  @Test
  public void testTimestampGetWarning() throws Exception {
    final String s = "<2013-12-31 12:30-19:12 -1d>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);

    assertEquals(12, ts.getWarningTime().getMonthOfYear());
    assertEquals(30, ts.getWarningTime().getDayOfMonth());
    assertEquals(2013, ts.getWarningTime().getYear());
    assertEquals(12, ts.getWarningTime().getHourOfDay());
    assertEquals(30, ts.getWarningTime().getMinuteOfHour());
  }

  @Test
  public void testTimestampNextRepeatSimple() throws Exception {
    // year
    OrgTimestamp ts = OrgTimestamp.fromString("<2013-12-31 12:30 +1y>");
    assertEquals(2013, ts.getDate().getYear());
    ts.toNextRepeat();
    assertEquals(2014, ts.getDate().getYear());
    // month
    ts.setRepeat("+1m");
    assertEquals(12, ts.getDate().getMonthOfYear());
    ts.toNextRepeat();
    assertEquals(2015, ts.getDate().getYear());
    assertEquals(1, ts.getDate().getMonthOfYear());
    // week
    ts.setRepeat("+1w");
    assertEquals(31, ts.getDate().getDayOfMonth());
    ts.toNextRepeat();
    assertEquals(7, ts.getDate().getDayOfMonth());
    assertEquals(2, ts.getDate().getMonthOfYear());
    // day
    ts.setRepeat("+1d");
    ts.toNextRepeat();
    assertEquals(8, ts.getDate().getDayOfMonth());
    assertEquals(2, ts.getDate().getMonthOfYear());
    // hour
    ts.setRepeat("+1h");
    assertEquals(12, ts.getDate().getHourOfDay());
    ts.toNextRepeat();
    assertEquals(8, ts.getDate().getDayOfMonth());
    assertEquals(13, ts.getDate().getHourOfDay());

  }

  @Test
  public void testTimestampNextRepeatFutureToday() throws Exception {
    // year in the past
    OrgTimestamp ts;
    ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
    final LocalDateTime now = LocalDateTime.now();
    assertEquals(2001, ts.getDate().getYear());
    ts.toNextRepeat();
    assertTrue(now.getYear() <= ts.getDate().getYear());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
    ts.setRepeat(".+1m");
    ts.toNextRepeat();
    assertTrue(now.getMonthOfYear() <= ts.getDate().getMonthOfYear());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
    ts.setRepeat(".+1w");
    ts.toNextRepeat();
    assertTrue(now.getDayOfYear() <= ts.getDate().getDayOfYear());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
    ts.setRepeat(".+1d");
    ts.toNextRepeat();
    if (now.getMonthOfYear() == ts.getDate().getMonthOfYear()) {
      assertTrue(now.getDayOfMonth() <= ts.getDate().getDayOfMonth());
    } else {
      assertTrue(now.getDayOfMonth() > ts.getDate().getDayOfMonth());
    }

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
    ts.setRepeat(".+1h");
    ts.toNextRepeat();
    if (now.getDayOfYear() == ts.getDate().getDayOfYear())
      assertTrue(now.getHourOfDay() <= ts.getDate().getHourOfDay());
    else
      assertTrue(now.getHourOfDay() > ts.getDate().getHourOfDay());
  }

  @Test
  public void testTimestampNextRepeatFuture() throws Exception {
    // year in the past
    OrgTimestamp ts;
    ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1y>");
    assertNotNull(ts);
    final LocalDateTime now = LocalDateTime.now();
    assertEquals(2001, ts.getDate().getYear());
    ts.toNextRepeat();
    assertTrue(ts.getDate().isAfter(now));
    assertEquals(12, ts.getDate().getMonthOfYear());
    assertEquals(28, ts.getDate().getDayOfMonth());
    assertEquals(12, ts.getDate().getHourOfDay());
    assertEquals(30, ts.getDate().getMinuteOfHour());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1m>");
    assertNotNull(ts);
    ts.toNextRepeat();
    assertTrue(ts.getDate().isAfter(now));
    assertEquals(28, ts.getDate().getDayOfMonth());
    assertEquals(12, ts.getDate().getHourOfDay());
    assertEquals(30, ts.getDate().getMinuteOfHour());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1w>");
    assertNotNull(ts);
    int day = ts.getDate().getDayOfWeek();
    ts.toNextRepeat();
    assertTrue(ts.getDate().isAfter(now));
    assertEquals(day, ts.getDate().getDayOfWeek());
    assertEquals(12, ts.getDate().getHourOfDay());
    assertEquals(30, ts.getDate().getMinuteOfHour());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1d>");
    assertNotNull(ts);
    ts.toNextRepeat();
    assertTrue(ts.getDate().isAfter(now));
    assertEquals(12, ts.getDate().getHourOfDay());
    assertEquals(30, ts.getDate().getMinuteOfHour());

    ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1h>");
    assertNotNull(ts);
    ts.toNextRepeat();
    assertTrue(ts.getDate().isAfter(now));
    if (now.getMinuteOfHour() < 30) {
      assertEquals(now.getHourOfDay(), ts.getDate().getHourOfDay());
    } else if (now.getHourOfDay() + 1 < 24) {
      assertEquals(now.getHourOfDay() + 1, ts.getDate().getHourOfDay());
    }
    else {
       assertEquals(0, ts.getDate().getHourOfDay());
    }
    assertEquals(30, ts.getDate().getMinuteOfHour());
  }


/*
  public static void main(String[] args) {
    //print("testing");
    //print(OrgParser.getHeaderPattern("NEXT").toString());
    //print(OrgParser.parseTags(":bob:alice:sting:").length);
    //print(OrgParser.parseTags(":bob:alice:sting:"));
    Pattern p = OrgParser.getHeaderPattern("NEXT");
    Matcher m = p.matcher("* NEXT A simple title :bob:alice:");
    while (m.find()) {
      //print(m.group());
      print("Stars: ", m.group("stars"));
      print("Title: ", m.group("title"));
      print("Todo: ", m.group("todo"));
      print("Tags: ", m.group("tags"));
    }

    testfile();
    }*/
}
