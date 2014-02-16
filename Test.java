import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Test {

  static void print(final int... numbers) {
    for (final int n: numbers) {
      System.out.println(n);
    }
  }

  static void print(final String... strings) {
    for (final String s: strings) {
      System.out.print(s);
    }
    System.out.println("");
  }

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
  }
}
