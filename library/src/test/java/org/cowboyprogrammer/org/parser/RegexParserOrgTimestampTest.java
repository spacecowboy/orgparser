/*
 * Copyright (c) 2015 Jonas Kalderstam
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cowboyprogrammer.org.parser;

import org.cowboyprogrammer.org.OrgTimestamp;
import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class RegexParserOrgTimestampTest {

    private static RegexParser regexParser;

    @BeforeClass
    public static void setup() {
        regexParser = new RegexParser();
    }

    @Test
    public void testTimestampPatternFull() {
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31 Tue 12:21-14:59 ++1w -2d>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertEquals(m.group(RegexParser.TIMESTAMP_DAY_GROUP), "Tue");
        assertEquals(m.group(RegexParser.TIMESTAMP_TIME_GROUP), "12:21");
        assertEquals(m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP), "14:59");
        assertEquals(m.group(RegexParser.TIMESTAMP_REPEAT_GROUP), "++1w");
        assertEquals(m.group(RegexParser.TIMESTAMP_WARNING_GROUP), "-2d");
    }

    @Test
    public void testTimestampPatternScheduled() {
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher("SCHEDULED: <2013-12-31 Tue 12:21-14:59 ++1w -2d>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertEquals(m.group(RegexParser.TIMESTAMP_DAY_GROUP), "Tue");
        assertEquals(m.group(RegexParser.TIMESTAMP_TIME_GROUP), "12:21");
        assertEquals(m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP), "14:59");
        assertEquals(m.group(RegexParser.TIMESTAMP_REPEAT_GROUP), "++1w");
        assertEquals(m.group(RegexParser.TIMESTAMP_WARNING_GROUP), "-2d");
    }

    @Test
    public void testTimestampPatternMinimum() {
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(RegexParser.TIMESTAMP_DAY_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_TIME_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_REPEAT_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_WARNING_GROUP));
    }

    @Test
    public void testTimestampPatternParts() {
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31 12:30 -1d>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(RegexParser.TIMESTAMP_DAY_GROUP));
        assertEquals(m.group(RegexParser.TIMESTAMP_TIME_GROUP), "12:30");
        assertNull(m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_REPEAT_GROUP));
        assertEquals(m.group(RegexParser.TIMESTAMP_WARNING_GROUP), "-1d");
    }

    @Test
    public void testTimestampPatternSpaces() {
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher("  <2013-12-31 12:30 -1d>  ");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(RegexParser.TIMESTAMP_DAY_GROUP));
        assertEquals(m.group(RegexParser.TIMESTAMP_TIME_GROUP), "12:30");
        assertNull(m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(RegexParser.TIMESTAMP_REPEAT_GROUP));
        assertEquals(m.group(RegexParser.TIMESTAMP_WARNING_GROUP), "-1d");
    }

    @Test
    public void testTimestampToString1() throws Exception {
        final String s = "<2013-12-31>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue>", res);
    }

    @Test
    public void testTimestampToString2() throws Exception {
        final String s = "<2013-12-31 12:30>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30>", res);
    }

    @Test
    public void testTimestampToString3() throws Exception {
        final String s = "<2013-12-31 12:30 -1w>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 -1w>", res);
    }

    @Test
    public void testTimestampToString4() throws Exception {
        final String s = "<2013-12-31 12:30 ++4y>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 ++4y>", res);
    }

    @Test
    public void testTimestampPattern4a() throws Exception {
        final String s = "<2013-12-31 12:30 ++4m>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 ++4m>", res);
    }

    @Test
    public void testTimestampPattern4b() throws Exception {
        final String s = "<2013-12-31 12:30 ++4w>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 ++4w>", res);
    }

    @Test
    public void testTimestampPattern4c() throws Exception {
        final String s = "<2013-12-31 12:30 ++4d>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 ++4d>", res);
    }

    @Test
    public void testTimestampPattern4d() throws Exception {
        final String s = "<2013-12-31 12:30 ++4h>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30 ++4h>", res);
    }

    @Test
    public void testTimestampToString5Dur() throws Exception {
        final String s = "<2013-12-31 12:30-19:12 ++4d>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);
        final String res = ts.toString(Locale.ENGLISH);
        assertEquals("<2013-12-31 Tue 12:30-19:12 ++4d>", res);
    }

    @Test
    public void testTimestampGetWarning() throws Exception {
        final String s = "<2013-12-31 12:30-19:12 -1d>";
        Pattern p = RegexParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = regexParser.getTimestamp(s);

        assertEquals(12, ts.getWarningTime().getMonthOfYear());
        assertEquals(30, ts.getWarningTime().getDayOfMonth());
        assertEquals(2013, ts.getWarningTime().getYear());
        assertEquals(12, ts.getWarningTime().getHourOfDay());
        assertEquals(30, ts.getWarningTime().getMinuteOfHour());
    }

    @Test
    public void testTimestampNextRepeatSimple() throws Exception {
        // year
        OrgTimestamp ts = regexParser.getTimestamp("<2013-12-31 12:30 +1y>");
        assertEquals(2013, ts.getDate().getYear());
        ts.toNextRepeat();
        assertEquals(2014, ts.getDate().getYear());
        // month
        ts.setRepeat("+1m");
        assertEquals(12, ts.getDate().getMonthOfYear());
        ts.toNextRepeat();
        assertEquals(2015, ts.getDate().getYear());
        assertEquals(1, ts.getDate().getMonthOfYear());
        // week
        ts.setRepeat("+1w");
        assertEquals(31, ts.getDate().getDayOfMonth());
        ts.toNextRepeat();
        assertEquals(7, ts.getDate().getDayOfMonth());
        assertEquals(2, ts.getDate().getMonthOfYear());
        // day
        ts.setRepeat("+1d");
        ts.toNextRepeat();
        assertEquals(8, ts.getDate().getDayOfMonth());
        assertEquals(2, ts.getDate().getMonthOfYear());
        // hour
        ts.setRepeat("+1h");
        assertEquals(12, ts.getDate().getHourOfDay());
        ts.toNextRepeat();
        assertEquals(8, ts.getDate().getDayOfMonth());
        assertEquals(13, ts.getDate().getHourOfDay());

    }

    @Test
    public void testTimestampNextRepeatFutureToday() throws Exception {
        // year in the past
        OrgTimestamp ts;
        ts = regexParser.getTimestamp("<2001-12-28 12:30 .+1y>");
        final LocalDateTime now = LocalDateTime.now();
        assertEquals(2001, ts.getDate().getYear());
        ts.toNextRepeat();
        assertTrue(now.getYear() <= ts.getDate().getYear());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1m");
        ts.toNextRepeat();
        assertTrue(now.getMonthOfYear() <= ts.getDate().getMonthOfYear());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1w");
        ts.toNextRepeat();
        assertTrue(now.getDayOfYear() <= ts.getDate().getDayOfYear());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1d");
        ts.toNextRepeat();
        if (now.getMonthOfYear() == ts.getDate().getMonthOfYear()) {
            assertTrue(now.getDayOfMonth() <= ts.getDate().getDayOfMonth());
        } else {
            assertTrue(now.getDayOfMonth() > ts.getDate().getDayOfMonth());
        }

        ts = regexParser.getTimestamp("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1h");
        ts.toNextRepeat();
        if (now.getDayOfYear() == ts.getDate().getDayOfYear())
            assertTrue(now.getHourOfDay() <= ts.getDate().getHourOfDay());
        else
            assertTrue(now.getHourOfDay() > ts.getDate().getHourOfDay());
    }

    @Test
    public void testTimestampNextRepeatFuture() throws Exception {
        // year in the past
        OrgTimestamp ts;
        ts = regexParser.getTimestamp("<2001-12-28 12:30 ++1y>");
        assertNotNull(ts);
        final LocalDateTime now = LocalDateTime.now();
        assertEquals(2001, ts.getDate().getYear());
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(12, ts.getDate().getMonthOfYear());
        assertEquals(28, ts.getDate().getDayOfMonth());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 ++1m>");
        assertNotNull(ts);
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(28, ts.getDate().getDayOfMonth());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 ++1w>");
        assertNotNull(ts);
        int day = ts.getDate().getDayOfWeek();
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(day, ts.getDate().getDayOfWeek());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 ++1d>");
        assertNotNull(ts);
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = regexParser.getTimestamp("<2001-12-28 12:30 ++1h>");
        assertNotNull(ts);
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        if (now.getMinuteOfHour() < 30) {
            assertEquals(now.getHourOfDay(), ts.getDate().getHourOfDay());
        } else if (now.getHourOfDay() + 1 < 24) {
            assertEquals(now.getHourOfDay() + 1, ts.getDate().getHourOfDay());
        } else {
            assertEquals(0, ts.getDate().getHourOfDay());
        }
        assertEquals(30, ts.getDate().getMinuteOfHour());
    }
}