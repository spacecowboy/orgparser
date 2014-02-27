package org.cowboyprogrammer.org;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OrgParser {

  /**
   * Get a regular expression pattern that includes all the possible
   * todo keywords. Matches are placed in named groups:
   * stars, todo, title, tags
   */
  public static Pattern getHeaderPattern(final String... todoKeys) {
    final StringBuilder sb = new StringBuilder();
    sb.append("^(?<stars>\\*+)"); // Leading stars
    sb.append("(\\s+(?<todo>TODO|DONE"); // TODO and DONE hardcoded
    for (final String key: todoKeys) {
      if (key.isEmpty()) continue;
      // Enforce upper case for keys
      sb.append("|").append(key.toUpperCase()); // Add this key
    }
    sb.append("))?"); // TODO keys are optional
    //sb.append("(?<prio>\\s+\\[#[A-C]\\])?"); // Optional priority
    sb.append("\\s+(?<title>.+?)"); // Title
    sb.append("(\\s+(?<tags>:.+:))?"); // Optional Tags
    sb.append("\\s*$"); // End of line
    return Pattern.compile(sb.toString());
  }

  /**
   * Returns a pattern that will match timestamps. Resulting groups
   * are in order of appearance (only date is mandatory):
   *
   * date, day, time, timeend, repeat, warning
   *
   * Example containing all elements:
   *
   * <2013-12-31 Tue 12:21-14:59 +1w -2d>
   *
   * In this case, the following would be the matched groups:
   *
   * date = 2013-12-31
   * day = Tue
   * time = 12:21
   * timeend = 14:59
   * repeat = +1w
   * warning = -2d
   */
  public static Pattern getTimestampPattern() {
    final StringBuilder sb = new StringBuilder();
    // Start
    sb.append("^\\s*[\\[<]")
    // Mandatory date
      .append("(?<date>\\d\\d\\d\\d-\\d\\d-\\d\\d)")
    // Optional start
      .append("(")
    // day, not number or space
      .append("(\\s+(?<day>[^\\d\\s]+))?")
    // time (optional duration)
      .append("(\\s+(?<time>\\d\\d:\\d\\d)(-(?<timeend>\\d\\d:\\d\\d))?)?")
    // repeater
      .append("(\\s+(?<repeat>[\\.\\+]?\\+\\d+[dwmy]))?")
    // warning
      .append("(\\s+(?<warning>-\\d+[dwmy]))?")
    // Optional end
      .append(")?")
    // End
      .append("[\\]>]\\s*$");

    return Pattern.compile(sb.toString());
  }

  /**
   * Given a tag-string like ':bob:alice:frank:', returns
   * a list splitted on :, e.g. [bob, alice, frank].
   *
   * Leading/ending spaces are OK.
   */
  public static String[] parseTags(final String otags) {
    if (otags == null) return null;

    final String tags = otags.trim();
    if (!tags.startsWith(":") || !tags.endsWith(":")) {
      throw new InvalidParameterException("Tag string must" +
                                          " start/end with ':'");
    }

    return tags.substring(1).split(":");
  }

}
