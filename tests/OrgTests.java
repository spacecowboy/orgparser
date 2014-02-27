package tests;

import org.cowboyprogrammer.org.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

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
    final String s = "<2013-12-31 12:30 ++4d>";
    Pattern p = OrgParser.getTimestampPattern();
    Matcher m = p.matcher(s);
    assertTrue(m.matches());
    OrgTimestamp ts = new OrgTimestamp(m);
    final String res = ts.toString();
    assertEquals("<2013-12-31 Tue 12:30 ++4d>", res);
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
