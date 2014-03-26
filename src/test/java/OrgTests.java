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

import org.cowboyprogrammer.org.OrgFile;
import org.cowboyprogrammer.org.OrgNode;
import org.cowboyprogrammer.org.OrgParser;
import org.cowboyprogrammer.org.OrgTimestamp;
import org.cowboyprogrammer.org.OrgTimestampRange;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class OrgTests {

    @Test
    public void thisAlwaysPasses() {

    }

    static void print(final int... numbers) {
        for (final int n : numbers) {
            System.out.println(n);
            System.out.print(" ");
        }
    }

    static void print(final Collection<String> strings) {
        for (final String s : strings) {
            System.out.print(s);
            System.out.print(" ");
        }
        System.out.println("");
    }

    static void print(final String... strings) {
        for (final String s : strings) {
            System.out.print(s);
            System.out.print(" ");
        }
        System.out.println("");
    }

    @Test
    @Ignore
    public void testfile() {
        final String fname = "test.org";

        try {

            final OrgFile root = OrgFile.createFromFile(fname);
            print("\n\n");
            print(root.treeToString());

            OrgNode leaf = root;
            while (leaf != null) {
                print(leaf.getAllTags());
                if (leaf.getSubNodes().isEmpty()) {
                    leaf = null;
                } else {
                    leaf = leaf.getSubNodes().get(0);
                }
            }

            writeToFile("test-out.org", root);

        } catch (FileNotFoundException e) {
            print(e.getMessage());
        } catch (IOException e) {
            print(e.getMessage());
        } catch (ParseException e) {
            print(e.getMessage());
        }
    }

    private void writeToFile(String filepath, OrgFile root) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
        root.writeToBuffer(bw);
        bw.close();
    }

    @Test
    public void testParsingBody() {
        // Check if body grows
        final String orgBody = "Body of two lines with\nno ending newline";
        final String orgEntry = "* Simple header\n" + orgBody;

        try {
            OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
            assertEquals(orgBody, orgFile1.getSubNodes().get(0).getBody());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getLocalizedMessage(), false);
        }
    }

    @Test
    public void testParsingBodies() {
        // Check if body grows
        final String orgHeader = "* Simple header\n";
        final String orgBody = "Body of two lines with\nending new line";
        final String orgEntry = orgHeader + orgBody + "\n" + orgHeader +
                orgBody + "\n";

        try {
            OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
            assertEquals(orgBody, orgFile1.getSubNodes().get(0).getBody());
            assertEquals(orgBody, orgFile1.getSubNodes().get(1).getBody());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getLocalizedMessage(), false);
        }
    }

    @Test
    public void testRepeatedParsedBodies() {
        // Check if body grows
        final String orgBody = "Body of two lines with\nno ending newline";
        final String orgEntry = "* Simple header\n" + orgBody;

        try {
            OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
            OrgFile orgFile2 = OrgFile.createFromString("test.org", orgFile1.treeToString());
            OrgFile orgFile3 = OrgFile.createFromString("test.org",
                    orgFile2.treeToString());
            assertEquals(orgBody, orgFile3.getSubNodes().get(0).getBody());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getLocalizedMessage(), false);
        }
    }

    @Test
    public void testCommentPrefix() {
        String commentline = "# NONSENSEID= 24SFS2";
        Matcher mc = OrgParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());

        commentline = "   # NONSENSEID= 24SFS2  ";
        mc = OrgParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());

        commentline = "# NONSENSEID= 24SFS2  \n";
        mc = OrgParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());
    }

    @Test
    public void testComments() {
        OrgNode node = new OrgNode();
        final String commentline = "# NONSENSEID= 24SFS2";
        final String normalline = "Bob bob";
        try {
            node.addBodyLine(commentline + "\n");
            node.addBodyLine(normalline + "\n");

            assertEquals(commentline + "\n", node.getComments());
            assertEquals(normalline + "\n", node.getBody());

        } catch (ParseException e) {
            assertTrue(false);
        }

    }

    @Test
    public void testDefaultHeaderPattern() {
        Pattern p = OrgParser.getHeaderPattern();
        Matcher m = p.matcher("* BOB A simple title :bob:alice:");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.HEADER_STARS_GROUP), "*");
        assertEquals(m.group(OrgParser.HEADER_TITLE_GROUP), "BOB A simple title");
        assertNull(m.group(OrgParser.HEADER_TODO_GROUP));
        assertEquals(m.group(OrgParser.HEADER_TAGS_GROUP), ":bob:alice:");
    }

    @Test
    public void testHeaderPatternTODO() {
        Pattern p = OrgParser.getHeaderPattern("BOB");
        Matcher m = p.matcher("* BOB A simple title :bob:alice:");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.HEADER_STARS_GROUP), "*");
        assertEquals(m.group(OrgParser.HEADER_TITLE_GROUP), "A simple title");
        assertEquals(m.group(OrgParser.HEADER_TODO_GROUP), "BOB");
        assertEquals(m.group(OrgParser.HEADER_TAGS_GROUP), ":bob:alice:");
    }

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
        assertEquals(sf, tf.toString());

        final String sd = "<2013-12-31 Tue>--<2014-02-28 Fri>";
        OrgTimestampRange td = OrgTimestampRange.fromString(sd);
        assertEquals(sd, td.toString());

        // Incomplete ones
        OrgTimestampRange t1 = OrgTimestampRange
                .fromString("<2013-12-31 Tue>--<2014-02-28 12:29>");
        assertEquals(sd, t1.toString());

        OrgTimestampRange t2 = OrgTimestampRange
                .fromString("<2013-12-31 13:25>--<2014-02-28>");
        assertEquals(sd, t2.toString());

    }

    @Test
    public void testTimestampPatternFull() {
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31 Tue 12:21-14:59 ++1w -2d>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertEquals(m.group(OrgParser.TIMESTAMP_DAY_GROUP), "Tue");
        assertEquals(m.group(OrgParser.TIMESTAMP_TIME_GROUP), "12:21");
        assertEquals(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP), "14:59");
        assertEquals(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP), "++1w");
        assertEquals(m.group(OrgParser.TIMESTAMP_WARNING_GROUP), "-2d");
    }

    @Test
    public void testTimestampPatternScheduled() {
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher("SCHEDULED: <2013-12-31 Tue 12:21-14:59 ++1w -2d>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertEquals(m.group(OrgParser.TIMESTAMP_DAY_GROUP), "Tue");
        assertEquals(m.group(OrgParser.TIMESTAMP_TIME_GROUP), "12:21");
        assertEquals(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP), "14:59");
        assertEquals(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP), "++1w");
        assertEquals(m.group(OrgParser.TIMESTAMP_WARNING_GROUP), "-2d");
    }

    @Test
    public void testTimestampPatternMinimum() {
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(OrgParser.TIMESTAMP_DAY_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_TIME_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_WARNING_GROUP));
    }

    @Test
    public void testTimestampPatternParts() {
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher("<2013-12-31 12:30 -1d>");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(OrgParser.TIMESTAMP_DAY_GROUP));
        assertEquals(m.group(OrgParser.TIMESTAMP_TIME_GROUP), "12:30");
        assertNull(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP));
        assertEquals(m.group(OrgParser.TIMESTAMP_WARNING_GROUP), "-1d");
    }

    @Test
    public void testTimestampPatternSpaces() {
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher("  <2013-12-31 12:30 -1d>  ");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.TIMESTAMP_DATE_GROUP), "2013-12-31");
        assertNull(m.group(OrgParser.TIMESTAMP_DAY_GROUP));
        assertEquals(m.group(OrgParser.TIMESTAMP_TIME_GROUP), "12:30");
        assertNull(m.group(OrgParser.TIMESTAMP_TIMEEND_GROUP));
        assertNull(m.group(OrgParser.TIMESTAMP_REPEAT_GROUP));
        assertEquals(m.group(OrgParser.TIMESTAMP_WARNING_GROUP), "-1d");
    }

    @Test
    public void testTimestampToString1() throws Exception {
        final String s = "<2013-12-31>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue>", res);
    }

    @Test
    public void testTimestampToString2() throws Exception {
        final String s = "<2013-12-31 12:30>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30>", res);
    }

    @Test
    public void testTimestampToString3() throws Exception {
        final String s = "<2013-12-31 12:30 -1w>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 -1w>", res);
    }

    @Test
    public void testTimestampToString4() throws Exception {
        final String s = "<2013-12-31 12:30 ++4y>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 ++4y>", res);
    }

    @Test
    public void testTimestampPattern4a() throws Exception {
        final String s = "<2013-12-31 12:30 ++4m>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 ++4m>", res);
    }

    @Test
    public void testTimestampPattern4b() throws Exception {
        final String s = "<2013-12-31 12:30 ++4w>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 ++4w>", res);
    }

    @Test
    public void testTimestampPattern4c() throws Exception {
        final String s = "<2013-12-31 12:30 ++4d>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 ++4d>", res);
    }

    @Test
    public void testTimestampPattern4d() throws Exception {
        final String s = "<2013-12-31 12:30 ++4h>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30 ++4h>", res);
    }

    @Test
    public void testTimestampToString5Dur() throws Exception {
        final String s = "<2013-12-31 12:30-19:12 ++4d>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);
        final String res = ts.toString();
        assertEquals("<2013-12-31 Tue 12:30-19:12 ++4d>", res);
    }

    @Test
    public void testTimestampGetWarning() throws Exception {
        final String s = "<2013-12-31 12:30-19:12 -1d>";
        Pattern p = OrgParser.getTimestampPattern();
        Matcher m = p.matcher(s);
        assertTrue(m.matches());
        OrgTimestamp ts = new OrgTimestamp(m);

        assertEquals(12, ts.getWarningTime().getMonthOfYear());
        assertEquals(30, ts.getWarningTime().getDayOfMonth());
        assertEquals(2013, ts.getWarningTime().getYear());
        assertEquals(12, ts.getWarningTime().getHourOfDay());
        assertEquals(30, ts.getWarningTime().getMinuteOfHour());
    }

    @Test
    public void testTimestampNextRepeatSimple() throws Exception {
        // year
        OrgTimestamp ts = OrgTimestamp.fromString("<2013-12-31 12:30 +1y>");
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
        ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
        final LocalDateTime now = LocalDateTime.now();
        assertEquals(2001, ts.getDate().getYear());
        ts.toNextRepeat();
        assertTrue(now.getYear() <= ts.getDate().getYear());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1m");
        ts.toNextRepeat();
        assertTrue(now.getMonthOfYear() <= ts.getDate().getMonthOfYear());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1w");
        ts.toNextRepeat();
        assertTrue(now.getDayOfYear() <= ts.getDate().getDayOfYear());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
        ts.setRepeat(".+1d");
        ts.toNextRepeat();
        if (now.getMonthOfYear() == ts.getDate().getMonthOfYear()) {
            assertTrue(now.getDayOfMonth() <= ts.getDate().getDayOfMonth());
        } else {
            assertTrue(now.getDayOfMonth() > ts.getDate().getDayOfMonth());
        }

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 .+1y>");
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
        ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1y>");
        assertNotNull(ts);
        final LocalDateTime now = LocalDateTime.now();
        assertEquals(2001, ts.getDate().getYear());
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(12, ts.getDate().getMonthOfYear());
        assertEquals(28, ts.getDate().getDayOfMonth());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1m>");
        assertNotNull(ts);
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(28, ts.getDate().getDayOfMonth());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1w>");
        assertNotNull(ts);
        int day = ts.getDate().getDayOfWeek();
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(day, ts.getDate().getDayOfWeek());
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1d>");
        assertNotNull(ts);
        ts.toNextRepeat();
        assertTrue(ts.getDate().isAfter(now));
        assertEquals(12, ts.getDate().getHourOfDay());
        assertEquals(30, ts.getDate().getMinuteOfHour());

        ts = OrgTimestamp.fromString("<2001-12-28 12:30 ++1h>");
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

  /*
    public static void main(String[] args) {
      //print("testing");
      //print(OrgParser.getHeaderPattern("NEXT").toString());
      //print(OrgParser.parseTags(":bob:alice:sting:").length);
      //print(OrgParser.parseTags(":bob:alice:sting:"));
      Pattern p = OrgParser.getHeaderPattern("NEXT");
      Matcher m = p.matcher("* NEXT A simple title :bob:alice:");
      while (m.find()) {
        //print(m.group());
        print("Stars: ", m.group(OrgParser.HEADER_STARS_GROUP));
        print("Title: ", m.group(OrgParser.HEADER_TITLE_GROUP));
        print("Todo: ", m.group(OrgParser.HEADER_TODO_GROUP));
        print("Tags: ", m.group(OrgParser.HEADER_TAGS_GROUP));
      }

      testfile();
      }*/
}
