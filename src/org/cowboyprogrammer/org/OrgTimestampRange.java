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

  public static final String OUTDATEFORMAT = "yyyy-MM-dd EEE";
  public static final String OUTDATETIMEFORMAT = "yyyy-MM-dd EEE HH:mm";

  public static final DateTimeFormatter INDATEFORMAT = DateTimeFormat
      .forPattern("yyyy-MM-dd");
  public static final DateTimeFormatter INTIMEFORMAT = DateTimeFormat
      .forPattern("HH:mm");

  // Please note that date represents local time
  private LocalDateTime startdate;
  private LocalDateTime enddate;

  private boolean hasTime = false;

  /**
   * Parse an org-timestamp
   */
  public static OrgTimestampRange fromString(final String s) {
    final Matcher m = OrgParser.getTimestampRangePattern().matcher(s);
    if (m.matches()) {
      return new OrgTimestampRange(m);
    } else {
      return null;
    }
  }

  public OrgTimestampRange() {
  }

  /**
   * Matcher is expected to have the same groups as the pattern from
   * OrgParser will give.
   */
  public OrgTimestampRange(final Matcher m) {
    this();

    startdate = INDATEFORMAT.parseLocalDateTime(m.group("startdate"));
    enddate = INDATEFORMAT.parseLocalDateTime(m.group("enddate"));

    if (null != m.group("starttime") && null != m.group("endtime")) {
      final LocalTime starttime = INTIMEFORMAT.parseLocalTime(m
          .group("starttime"));
      startdate = startdate.withTime(starttime.getHourOfDay(),
          starttime.getMinuteOfHour(), 0, 0);

      final LocalTime endtime = INTIMEFORMAT.parseLocalTime(m.group("endtime"));
      enddate = enddate.withTime(endtime.getHourOfDay(),
          endtime.getMinuteOfHour(), 0, 0);

      hasTime = true;
    }
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder("<");
    if (hasTime) {
      sb.append(startdate.toString(OUTDATETIMEFORMAT));
    } else {
      sb.append(startdate.toString(OUTDATEFORMAT));
    }
    sb.append(">--<");
    if (hasTime) {
      sb.append(enddate.toString(OUTDATETIMEFORMAT));
    } else {
      sb.append(enddate.toString(OUTDATEFORMAT));
    }
    sb.append(">");

    return sb.toString();
  }

  public boolean isHasTime() {
    return hasTime;
  }

  public LocalDateTime getStartdate() {
    return startdate;
  }

  public void setStartdate(final LocalDateTime startdate, final boolean withTime) {
    this.startdate = startdate;
    hasTime = withTime;
  }

  public LocalDateTime getEnddate() {
    return enddate;
  }

  public void setEnddate(final LocalDateTime enddate, final boolean withTime) {
    this.enddate = enddate;
    hasTime = withTime;
  }

}
