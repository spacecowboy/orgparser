/*
 * Copyright (c) Jonas Kalderstam 2014.
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

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;
import java.util.regex.Matcher;

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

        startdate = INDATEFORMAT.parseLocalDateTime(m
                .group(OrgParser.TIMESTAMPRANGE_STARTDATE_GROUP));
        enddate = INDATEFORMAT.parseLocalDateTime(m.group(OrgParser.TIMESTAMPRANGE_ENDDATE_GROUP));

        if (null != m.group(OrgParser.TIMESTAMPRANGE_STARTTIME_GROUP)
                && null != m.group(OrgParser.TIMESTAMPRANGE_ENDTIME_GROUP)) {
            final LocalTime starttime = INTIMEFORMAT.parseLocalTime(m
                    .group(OrgParser.TIMESTAMPRANGE_STARTTIME_GROUP));
            startdate = startdate.withTime(starttime.getHourOfDay(),
                    starttime.getMinuteOfHour(), 0, 0);

            final LocalTime endtime = INTIMEFORMAT.parseLocalTime(m.group(OrgParser.TIMESTAMPRANGE_ENDTIME_GROUP));
            enddate = enddate.withTime(endtime.getHourOfDay(),
                    endtime.getMinuteOfHour(), 0, 0);

            hasTime = true;
        }
    }

    public String toString() {
        return toString(Locale.getDefault());
    }

    public String toString(Locale locale) {
        final StringBuilder sb = new StringBuilder("<");
        if (hasTime) {
            sb.append(startdate.toString(OUTDATETIMEFORMAT, locale));
        } else {
            sb.append(startdate.toString(OUTDATEFORMAT, locale));
        }
        sb.append(">--<");
        if (hasTime) {
            sb.append(enddate.toString(OUTDATETIMEFORMAT, locale));
        } else {
            sb.append(enddate.toString(OUTDATEFORMAT, locale));
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
