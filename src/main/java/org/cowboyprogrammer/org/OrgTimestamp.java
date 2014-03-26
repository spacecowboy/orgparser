/*
 * Copyright (c) 2014.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cowboyprogrammer.org;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.regex.Matcher;

/**
 * This class represents a single timestamp. Not a duration.
 */
public class OrgTimestamp {

    public static final String OUTDATEFORMAT = "yyyy-MM-dd EEE";
    public static final String OUTDATETIMEFORMAT = "yyyy-MM-dd EEE HH:mm";
    public static final String OUTENDTIMEFORMAT = "-HH:mm";

    public static final DateTimeFormatter INDATEFORMAT = DateTimeFormat
            .forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter INTIMEFORMAT = DateTimeFormat
            .forPattern("HH:mm");

    public static enum Type {
        PLAIN, DEADLINE, SCHEDULED
    }

    private Type type = Type.PLAIN;

    // Please note that date represents local time
    private LocalDateTime date;
    // Just the end time
    private LocalTime endTime = null;

    // if timestamp includes a time. <2013-12-31> vs <2013-12-31 22:31>
    private boolean hasTime = false;

    // Example: +3y or ++3d or .+3w
    private String repeater = null;
    private ReadablePeriod repeatPeriod = null;

    // Example: -2d
    private String warning = null;
    private ReadablePeriod warningPeriod = null;

    // Decides braces: (false) <> vs [] (true)
    private boolean inactive = false;

    public OrgTimestamp() {
    }

    /**
     * Parse an org-timestamp
     */
    public static OrgTimestamp fromString(final String s) {
        final Matcher m = OrgParser.getTimestampPattern().matcher(s);
        if (m.matches()) {
            return new OrgTimestamp(m);
        } else {
            return null;
        }
    }

    /**
     * @param millis   Milliseconds since the epoch.
     * @param withTime true if time part is considered valid.
     */
    public OrgTimestamp(final long millis, final boolean withTime) {
        this();
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        date = LocalDateTime.fromCalendarFields(cal);
        hasTime = withTime;
    }

    /**
     * Matcher is expected to have the same groups as the pattern from
     * OrgParser will give.
     */
    public OrgTimestamp(final Matcher m) {
        this();

        date = INDATEFORMAT.parseLocalDateTime(m.group(OrgParser.TIMESTAMP_DATE_GROUP));

        if ("[".equals(m.group(OrgParser.TIMESTAMP_ACTIVE_GROUP))) {
            inactive = true;
        }

        if (null != m.group(OrgParser.TIMESTAMP_TYPE_GROUP)) {
            final String t = m.group(OrgParser.TIMESTAMP_TYPE_GROUP);
            if (t.equals("DEADLINE")) {
                type = Type.DEADLINE;
            } else if (t.equals("SCHEDULED")) {
                type = Type.SCHEDULED;
            }
        }

        if (null != m.group(OrgParser.TIMESTAMP_TIME_GROUP)) {
            final LocalTime time = INTIMEFORMAT.parseLocalTime(m.group(OrgParser.TIMESTAMP_TIME_GROUP));
            date = date.withTime(time.getHourOfDay(), time.getMinuteOfHour(), 0, 0);
            hasTime = true;

            if (null != m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP)) {
                setEndTime(INTIMEFORMAT.parseLocalTime(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP)));
            }
        }

        if (null != m.group(OrgParser.TIMESTAMP_WARNING_GROUP)) {
            setWarning(m.group(OrgParser.TIMESTAMP_WARNING_GROUP));
        }

        if (null != m.group(OrgParser.TIMESTAMP_REPEAT_GROUP)) {
            setRepeat(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP));
        }
    }

    /**
     * Move this timestamp one repetition.
     */
    public void toNextRepeat() {
        if (repeater != null) {
            if (repeater.startsWith("++")) {
                final LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(date)) {
                    // Just get it into the future
                    while (now.isAfter(date)) {
                        date = date.plus(repeatPeriod);
                    }
                } else {
                    // Already in future, just jump
                    date = date.plus(repeatPeriod);
                }
            } else if (repeater.startsWith(".+")) {
                // Count from NOW
                date = LocalDateTime.now().plus(repeatPeriod);
            } else { // +
                date = date.plus(repeatPeriod);
            }
        }
    }

    /**
     * Return the next repetition of this time, even if
     * it is already in the future. Null if no repeat.
     */
    public LocalDateTime getNextRepetition() {
        if (repeater == null)
            return null;

        final LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = date.withDayOfMonth(date.getDayOfMonth());

        if (repeater.startsWith("++")) {
            if (now.isAfter(next)) {
                // Just get it into the future
                while (now.isAfter(next)) {
                    next = next.plus(repeatPeriod);
                }
            } else {
                // Already in future, just jump
                next = next.plus(repeatPeriod);
            }
        } else if (repeater.startsWith(".+")) {
            // Count from NOW
            next = now.plus(repeatPeriod);
        } else { // + or
            next = next.plus(repeatPeriod);
        }

        return next;
    }

    /**
     * Returns null if no repeater is set. Otherwise the next repetition of this
     * time which is in the future. If it is already in the future, it will
     * return that.
     */
    public LocalDateTime getNextFutureRepetition() {
        if (repeater == null) {
            return null;
        }
        final LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(date)) {
            // Already in future
            return date;
        }
        // In this case, + and ++ have the same behaviour
        if (repeater.startsWith("+")) {
            LocalDateTime next = date.plus(repeatPeriod);
            // Just get it into the future
            while (now.isAfter(next)) {
                next = next.plus(repeatPeriod);
            }
            return next;
        } else {
            // Count from NOW
            return now.plus(repeatPeriod);
        }
    }

    public LocalDateTime getWarningTime() {
        if (warning != null) {
            return date.minus(warningPeriod);
        }
        return null;
    }

    public void setWarning(final String warning) {
        this.warning = warning;
        warningPeriod = parsePeriod(
                Integer.parseInt(warning.substring(1, warning.length() - 1)),
                warning.substring(warning.length() - 1));
    }

    public void setRepeat(final String repeat) {
        this.repeater = repeat;
        int start = 1;
        if ("+".equals(repeat.substring(1, 2))) {
            start = 2;
        }
        repeatPeriod = parsePeriod(
                Integer.parseInt(repeat.substring(start, repeat.length() - 1)),
                repeat.substring(repeat.length() - 1));
    }

    protected ReadablePeriod parsePeriod(final int t, final String w) {
        final ReadablePeriod p;

        if (w.equals("h")) {
            p = Hours.hours(t);
        } else if (w.equals("d")) {
            p = Days.days(t);
        } else if (w.equals("w")) {
            p = Weeks.weeks(t);
        } else if (w.equals("m")) {
            p = Months.months(t);
        } else {
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
            if (getEndTime() != null) {
                sb.append(getEndTime().toString(OUTENDTIMEFORMAT));
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

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public void setDate(final LocalDateTime date, final boolean withTime) {
        if (date == null) {
            throw new NullPointerException("Date can't be null!");
        }
        this.date = date;
        hasTime = withTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(final LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean hasTime() {
        return hasTime;
    }

    public String getRepeat() {
        return repeater;
    }

    public ReadablePeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public ReadablePeriod getWarningPeriod() {
        return warningPeriod;
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(final boolean inactive) {
        this.inactive = inactive;
    }

}
