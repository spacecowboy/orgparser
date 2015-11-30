/*
 * Copyright (c) 2015. Jonas Kalderstam
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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class OrgTimestampRangeTest {

    @Test
    public void testHasNoTime() throws Exception {
        OrgTimestampRange r = new OrgTimestampRange("2016-01-01", "2016-02-02",null, null);
        assertFalse("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2016, r.getStartdate().getYear());
        assertEquals("Wrong end month", 1, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 1, r.getStartdate().getDayOfMonth());

        assertEquals("Wrong end year", 2016, r.getEnddate().getYear());
        assertEquals("Wrong end month", 2, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 2, r.getEnddate().getDayOfMonth());

        r = new OrgTimestampRange("2016-01-01", "2016-02-02","00:00", null);
        assertFalse("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2016, r.getStartdate().getYear());
        assertEquals("Wrong end month", 1, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 1, r.getStartdate().getDayOfMonth());

        assertEquals("Wrong end year", 2016, r.getEnddate().getYear());
        assertEquals("Wrong end month", 2, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 2, r.getEnddate().getDayOfMonth());

        r = new OrgTimestampRange("2016-01-01", "2016-02-02", null, "00:00");
        assertFalse("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2016, r.getStartdate().getYear());
        assertEquals("Wrong end month", 1, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 1, r.getStartdate().getDayOfMonth());

        assertEquals("Wrong end year", 2016, r.getEnddate().getYear());
        assertEquals("Wrong end month", 2, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 2, r.getEnddate().getDayOfMonth());
    }

    @Test
    public void testHasTime() throws Exception {
        OrgTimestampRange r = new OrgTimestampRange("2016-01-01", "2016-02-02", "00:00", "00:00");

        assertTrue("Expecting a time", r.hasTime());

        assertEquals("Wrong end year", 2016, r.getStartdate().getYear());
        assertEquals("Wrong end month", 1, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 1, r.getStartdate().getDayOfMonth());
        assertEquals("Wrong hour", 0, r.getStartdate().getHourOfDay());
        assertEquals("Wrong minute", 0, r.getStartdate().getMinuteOfHour());

        assertEquals("Wrong end year", 2016, r.getEnddate().getYear());
        assertEquals("Wrong end month", 2, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 2, r.getEnddate().getDayOfMonth());
        assertEquals("Wrong hour", 0, r.getEnddate().getHourOfDay());
        assertEquals("Wrong minute", 0, r.getEnddate().getMinuteOfHour());
    }

    @Test
    public void testSetDates() throws Exception {
        OrgTimestampRange r = new OrgTimestampRange("2016-01-01", "2016-02-02",null, null);
        assertFalse("Expecting no time", r.hasTime());

        r.setStartdate(LocalDateTime.parse("2020-11-11"), false);
        assertFalse("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2020, r.getStartdate().getYear());
        assertEquals("Wrong end month", 11, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 11, r.getStartdate().getDayOfMonth());

        r.setEnddate(LocalDateTime.parse("2020-12-12"), false);
        assertFalse("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2020, r.getEnddate().getYear());
        assertEquals("Wrong end month", 12, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 12, r.getEnddate().getDayOfMonth());
    }

    @Test
    public void testSetDatesWithTime() throws Exception {
        OrgTimestampRange r = new OrgTimestampRange("2016-01-01", "2016-02-02",null, null);
        assertFalse("Expecting no time", r.hasTime());

        r.setStartdate(LocalDateTime.parse("2020-11-11T21:21"), true);
        assertTrue("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2020, r.getStartdate().getYear());
        assertEquals("Wrong end month", 11, r.getStartdate().getMonthOfYear());
        assertEquals("Wrong end day", 11, r.getStartdate().getDayOfMonth());
        assertEquals("Wrong hour", 21, r.getStartdate().getHourOfDay());
        assertEquals("Wrong minute", 21, r.getStartdate().getMinuteOfHour());

        r.setEnddate(LocalDateTime.parse("2020-12-12T21:21"), true);
        assertTrue("Expecting no time", r.hasTime());
        assertEquals("Wrong end year", 2020, r.getEnddate().getYear());
        assertEquals("Wrong end month", 12, r.getEnddate().getMonthOfYear());
        assertEquals("Wrong end day", 12, r.getEnddate().getDayOfMonth());
        assertEquals("Wrong hour", 21, r.getEnddate().getHourOfDay());
        assertEquals("Wrong minute", 21, r.getEnddate().getMinuteOfHour());
    }
}