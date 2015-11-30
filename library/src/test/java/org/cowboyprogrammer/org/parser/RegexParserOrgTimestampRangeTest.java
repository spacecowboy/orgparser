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

import org.cowboyprogrammer.org.OrgTimestampRange;
import org.cowboyprogrammer.org.parser.RegexParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class RegexParserOrgTimestampRangeTest {

    private static RegexParser regexParser;

    @BeforeClass
    public static void setup() {
        regexParser = new RegexParser();
    }

    @Test
    public void testTimestampRangePattern() {
        Pattern p = RegexParser.getTimestampRangePattern();
        String s = "<2013-12-31 Tue 12:21>--<2014-02-28 Wed 19:21>";
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTDAY_GROUP), "Tue");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTTIME_GROUP), "12:21");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDDAY_GROUP), "Wed");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDTIME_GROUP), "19:21");

        m = p.matcher("<2013-12-31 12:21>--<2014-02-28 19:21>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTTIME_GROUP), "12:21");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDTIME_GROUP), "19:21");

        m = p.matcher("<2013-12-31>--<2014-02-28>");
        assertTrue(m.matches());
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(RegexParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
    }

    @Test
    public void testTimestampRangeToString() {
        final String sf = "<2013-12-31 Tue 12:21>--<2014-02-28 Fri 19:21>";
        OrgTimestampRange tf = regexParser.getTimestampRange(sf);
        assertEquals(sf, tf.toString(Locale.ENGLISH));

        final String sd = "<2013-12-31 Tue>--<2014-02-28 Fri>";
        OrgTimestampRange td = regexParser.getTimestampRange(sd);
        assertEquals(sd, td.toString(Locale.ENGLISH));

        // Incomplete ones
        OrgTimestampRange t1 = regexParser.getTimestampRange("<2013-12-31 Tue>--<2014-02-28 12:29>");
        assertEquals(sd, t1.toString(Locale.ENGLISH));

        OrgTimestampRange t2 = regexParser.getTimestampRange("<2013-12-31 13:25>--<2014-02-28>");
        assertEquals(sd, t2.toString(Locale.ENGLISH));

    }
}