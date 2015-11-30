package org.cowboyprogrammer.org;

import org.junit.Test;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class OrgTimestampRangeTest {
    @Test
    public void testTimestampRangePattern() {
        Pattern p = OrgParser.getTimestampRangePattern();
        String s = "<2013-12-31 Tue 12:21>--<2014-02-28 Wed 19:21>";
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTDAY_GROUP), "Tue");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTTIME_GROUP), "12:21");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDDAY_GROUP), "Wed");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDTIME_GROUP), "19:21");

        m = p.matcher("<2013-12-31 12:21>--<2014-02-28 19:21>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTTIME_GROUP), "12:21");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDTIME_GROUP), "19:21");

        m = p.matcher("<2013-12-31>--<2014-02-28>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_STARTDATE_GROUP), "2013-12-31");
        assertEquals(m.group(OrgParser.TIMESTAMPRANGE_ENDDATE_GROUP), "2014-02-28");
    }

    @Test
    public void testTimestampRangeToString() {
        final String sf = "<2013-12-31 Tue 12:21>--<2014-02-28 Fri 19:21>";
        OrgTimestampRange tf = OrgTimestampRange.fromString(sf);
        assertEquals(sf, tf.toString(Locale.ENGLISH));

        final String sd = "<2013-12-31 Tue>--<2014-02-28 Fri>";
        OrgTimestampRange td = OrgTimestampRange.fromString(sd);
        assertEquals(sd, td.toString(Locale.ENGLISH));

        // Incomplete ones
        OrgTimestampRange t1 = OrgTimestampRange
                .fromString("<2013-12-31 Tue>--<2014-02-28 12:29>");
        assertEquals(sd, t1.toString(Locale.ENGLISH));

        OrgTimestampRange t2 = OrgTimestampRange
                .fromString("<2013-12-31 13:25>--<2014-02-28>");
        assertEquals(sd, t2.toString(Locale.ENGLISH));

    }
}