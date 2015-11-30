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

    public OrgTimestampRange() {
    }

    public OrgTimestampRange(final String startDate, final String endDate, final String startTime,
                             final String endTime) {
        this();
        startdate = INDATEFORMAT.parseLocalDateTime(startDate);
        enddate = INDATEFORMAT.parseLocalDateTime(endDate);

        if (null != startTime && null != endTime) {
            final LocalTime starttime = INTIMEFORMAT.parseLocalTime(startTime);
            startdate = startdate.withTime(starttime.getHourOfDay(), starttime.getMinuteOfHour(), 0, 0);

            final LocalTime endtime = INTIMEFORMAT.parseLocalTime(endTime);
            enddate = enddate.withTime(endtime.getHourOfDay(), endtime.getMinuteOfHour(), 0, 0);

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

    public boolean hasTime() {
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
