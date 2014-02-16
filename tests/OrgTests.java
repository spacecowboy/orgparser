package tests;

import org.cowboyprogrammer.org.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;


import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


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
