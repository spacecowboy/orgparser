package org.cowboyprogrammer.org;

import java.util.regex.Matcher;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This class represents a duration.
 */
public class OrgTimestampRange {

  static final String OUTDATEFORMAT="yyyy-MM-dd EEE";
  static final String OUTDATETIMEFORMAT="yyyy-MM-dd EEE HH:mm";

  static final DateTimeFormatter INDATEFORMAT =
    DateTimeFormat.forPattern("yyyy-MM-dd");
  static final DateTimeFormatter INTIMEFORMAT =
     DateTimeFormat.forPattern("HH:mm");

  // Please note that date represents local time
  protected LocalDateTime startdate;
  protected LocalDateTime enddate;

  protected boolean hasTime = false;


  /**
   * Parse an org-timestamp
   */
  public static OrgTimestampRange fromString(final String s) {
    final Matcher m = OrgParser.getTimestampRangePattern().matcher(s);
    if (m.matches()) {
      return new OrgTimestampRange(m);
    }
    else {
      return null;
    }
  }


  public OrgTimestampRange () {
  }

    /**
   * Matcher is expected to have the same groups as the pattern from
   * OrgParser will give.
   */
  public OrgTimestampRange (final Matcher m) {
    this();

    startdate = INDATEFORMAT.parseLocalDateTime(m.group("startdate"));
    enddate = INDATEFORMAT.parseLocalDateTime(m.group("enddate"));

    if (null != m.group("starttime") &&
        null != m.group("endtime")) {
      final LocalTime starttime =
        INTIMEFORMAT.parseLocalTime(m.group("starttime"));
      startdate = startdate.withTime(starttime.getHourOfDay(),
                                     starttime.getMinuteOfHour(), 0 ,0);

      final LocalTime endtime =
        INTIMEFORMAT.parseLocalTime(m.group("endtime"));
      enddate = enddate.withTime(endtime.getHourOfDay(),
                                     endtime.getMinuteOfHour(), 0 ,0);

      hasTime = true;
    }
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder("<");
    if (hasTime) {
      sb.append(startdate.toString(OUTDATETIMEFORMAT));
    }
    else {
      sb.append(startdate.toString(OUTDATEFORMAT));
    }
    sb.append(">--<");
    if (hasTime) {
      sb.append(enddate.toString(OUTDATETIMEFORMAT));
    }
    else {
      sb.append(enddate.toString(OUTDATEFORMAT));
    }
    sb.append(">");

    return sb.toString();
  }

}
