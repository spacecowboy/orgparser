package org.cowboyprogrammer.org;

import java.text.ParseException;
import java.util.regex.Matcher;

import org.joda.time.ReadablePeriod;
import org.joda.time.Hours;
import org.joda.time.Days;
import org.joda.time.Weeks;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * This class represents a single timestamp. Not a duration.
 */
public class OrgTimestamp {

  static final String OUTDATEFORMAT="yyyy-MM-dd EEE";
  static final String OUTDATETIMEFORMAT="yyyy-MM-dd EEE HH:mm";
  static final String OUTENDTIMEFORMAT="-HH:mm";

  static final DateTimeFormatter INDATEFORMAT =
    DateTimeFormat.forPattern("yyyy-MM-dd");
  static final DateTimeFormatter INTIMEFORMAT =
     DateTimeFormat.forPattern("HH:mm");

  public static enum Type {
    PLAIN, DEADLINE, SCHEDULED
  }
  public Type type = Type.PLAIN;

  // Please note that date represents local time
  protected LocalDateTime date;
  // Just the end time
  protected LocalTime endTime = null;

  // if timestamp includes a time. <2013-12-31> vs <2013-12-31 22:31>
  public boolean hasTime = false;

  // Example: +3y or ++3d or .+3w
  protected String repeater = null;
  protected ReadablePeriod repeatPeriod = null;

  // Example: -2d
  protected String warning = null;
  protected ReadablePeriod warningPeriod = null;

  // Decides braces: (false) <> vs [] (true)
  public boolean inactive = false;

  public OrgTimestamp () {
  }

  /**
   * Parse an org-timestamp
   */
  public static OrgTimestamp fromString(final String s) {
    final Matcher m = OrgParser.getTimestampPattern().matcher(s);
    if (m.matches()) {
      return new OrgTimestamp(m);
    }
    else {
      return null;
    }
  }

  /**
   * Matcher is expected to have the same groups as the pattern from
   * OrgParser will give.
   */
  public OrgTimestamp (final Matcher m) {
    this();

    date = INDATEFORMAT.parseLocalDateTime(m.group("date"));

    if (null != m.group("time")) {
      final LocalTime time = INTIMEFORMAT.parseLocalTime(m.group("time"));
      date = date.withTime(time.getHourOfDay(), time.getMinuteOfHour(), 0 ,0);
      hasTime = true;

      if (null != m.group("timeend")) {
        endTime = INTIMEFORMAT.parseLocalTime(m.group("timeend"));
      }
    }

    if (null != m.group("warning")) {
      setWarning(m.group("warning"));
    }

    if (null != m.group("repeat")) {
      setRepeat(m.group("repeat"));
    }
  }

  public void toNextRepeat() {
    if (repeater != null) {
      if (repeater.startsWith("++")) {
        final LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(date)) {
          // Just get it into the future
          while (now.isAfter(date)) {
            date = date.plus(repeatPeriod);
          }
        }
        else {
          // Already in future, just jump
          date = date.plus(repeatPeriod);
        }
      }
      else if (repeater.startsWith(".+")) {
        // Count from NOW
        date = LocalDateTime.now().plus(repeatPeriod);
      }
      else { // +
        date = date.plus(repeatPeriod);
      }
    }
    else {
      // Throw exception? Return false?
    }
  }

  public LocalDateTime getWarningTime() {
    if (warning != null) {
      return date.minus(warningPeriod);
    } else {
      // Throw exception? Return null? Return actual time?
    }
    return null;
  }

  public void setWarning(final String warning) {
    this.warning = warning;
    warningPeriod = parsePeriod(Integer.parseInt(warning.substring(1, warning.length() - 1)),
                                warning.substring(warning.length() - 1));
  }

  public void setRepeat(final String repeat) {
    this.repeater = repeat;
    int start = 1;
    if ("+".equals(repeat.substring(1, 2))) {
      start = 2;
    }
    repeatPeriod = parsePeriod(Integer.parseInt(repeat.substring(start, repeat.length() - 1)),
                                repeat.substring(repeat.length() - 1));
  }

  protected ReadablePeriod parsePeriod(final int t, final String w) {
    final ReadablePeriod p;

    if (w.equals("h")) {
      p = Hours.hours(t);
    }
    else if (w.equals("d")) {
      p = Days.days(t);
    }
    else if (w.equals("w")) {
      p = Weeks.weeks(t);
    }
    else if (w.equals("m")) {
      p = Months.months(t);
    }
    else {
      p = Years.years(t);
    }

    return p;
  }

  public LocalDateTime getDate() {
    return date;
  }

  /**
   * Returns the string format of this timestamp.
   */
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    switch (type) {
    case DEADLINE:
      sb.append("DEADLINE: ");
      break;
    case SCHEDULED:
      sb.append("SCHEDULED: ");
      break;
    }

    // Leading brace
    if (inactive) {
      sb.append("[");
    } else {
      sb.append("<");
    }

    if (hasTime) {
      // With time
      sb.append(date.toString(OUTDATETIMEFORMAT));
      if (endTime != null) {
        sb.append(endTime.toString(OUTENDTIMEFORMAT));
      }
    } else {
      // Only date
      sb.append(date.toString(OUTDATEFORMAT));
    }

    // Repeat comes before warning
    if (repeater != null) {
      sb.append(" ").append(repeater);
    }

    if (warning != null) {
      sb.append(" ").append(warning);
    }

    // Ending brace
    if (inactive) {
      sb.append("]");
    } else {
      sb.append(">");
    }

    return sb.toString();
  }
}
