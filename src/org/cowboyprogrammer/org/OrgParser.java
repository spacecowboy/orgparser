package org.cowboyprogrammer.org;

import java.security.InvalidParameterException;
import java.util.regex.Pattern;


public class OrgParser {

  /*
   * Can't use named groups because they are not supported in Android.
   */
  public static final int HEADER_STARS_GROUP = 1;
  public static final int HEADER_TODO_GROUP = 2;
  public static final int HEADER_TITLE_GROUP = 3;
  public static final int HEADER_TAGS_GROUP = 4;

  public static final int TIMESTAMP_TYPE_GROUP = 1;
  public static final int TIMESTAMP_ACTIVE_GROUP = 2;
  public static final int TIMESTAMP_DATE_GROUP = 3;
  public static final int TIMESTAMP_DAY_GROUP = 4;
  public static final int TIMESTAMP_TIME_GROUP = 5;
  public static final int TIMESTAMP_TIMEEND_GROUP = 6;
  public static final int TIMESTAMP_REPEAT_GROUP = 7;
  public static final int TIMESTAMP_WARNING_GROUP = 8;

  public static final int TIMESTAMPRANGE_STARTDATE_GROUP = 1;
  public static final int TIMESTAMPRANGE_STARTDAY_GROUP = 2;
  public static final int TIMESTAMPRANGE_STARTTIME_GROUP = 3;
  public static final int TIMESTAMPRANGE_ENDDATE_GROUP = 4;
  public static final int TIMESTAMPRANGE_ENDDAY_GROUP = 5;
  public static final int TIMESTAMPRANGE_ENDTIME_GROUP = 6;

  /**
   * Get a regular expression pattern that includes all the possible
   * todo keywords. Matches are placed in named groups:
   * stars, todo, title, tags
   */
  public static Pattern getHeaderPattern(final String... todoKeys) {
    final StringBuilder sb = new StringBuilder();
    sb.append("^(\\*+)"); // Leading stars
    sb.append("(?:\\s+(TODO|DONE"); // TODO and DONE hardcoded
    for (final String key: todoKeys) {
      if (key.isEmpty()) continue;
      // Enforce upper case for keys
      sb.append("|").append(key.toUpperCase()); // Add this key
    }
    sb.append("))?");
    //sb.append("(?<prio>\\s+\\[#[A-C]\\])?"); // Optional priority
    sb.append("\\s+(.+?)"); // Title
    sb.append("(?:\\s+(:.+:))?"); // Optional Tags
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
    // start of line
    sb.append("^")
      // Optional type
      .append("(?:(SCHEDULED|DEADLINE):)?")
    // Start of date
      .append("\\s*([\\[<])")
    // Mandatory date
      .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
    // Optional start
      .append("(?:")
    // day, not number or space
      .append("(?:\\s+([^\\d\\s]+))?")
    // time (optional duration)
      .append("(?:\\s+(\\d\\d:\\d\\d)(?:-(\\d\\d:\\d\\d))?)?")
    // repeater
      .append("(?:\\s+([\\.\\+]?\\+\\d+[hdwmy]))?")
    // warning
      .append("(?:\\s+(-\\d+[dwmy]))?")
    // Optional end
      .append(")?")
    // End
      .append("[\\]>]\\s*$");

    return Pattern.compile(sb.toString());
  }

/**
 * For timestamps such as:
 * <2014-01-28>--<2014-02-28>
 */
  public static Pattern getTimestampRangePattern() {
    final StringBuilder sb = new StringBuilder();
    // Start
    sb.append("^\\s*[<]")
    // Mandatory date
      .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
    // Optional start
      .append("(?:")
    // day, not number or space
      .append("(?:\\s+([^\\d\\s]+))?")
    // time
      .append("(?:\\s+(\\d\\d:\\d\\d))?")
    // Optional end
      .append(")?")
    // End
      .append("[>]")
    // Range
      .append("--")
    // Start2
      .append("[<]")
    // Mandatory date
      .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
    // Optional start
      .append("(?:")
    // day, not number or space
      .append("(?:\\s+([^\\d\\s]+))?")
    // time
      .append("(?:\\s+(\\d\\d:\\d\\d))?")
    // Optional end
      .append(")?")
    // End
      .append("[>]\\s*$");

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
