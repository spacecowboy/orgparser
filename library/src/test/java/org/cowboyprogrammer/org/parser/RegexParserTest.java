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

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cowboyprogrammer.org.util.StringUtils.reverse;
import static org.junit.Assert.*;


public class RegexParserTest {

    @Test
    public void testCommentPrefix() throws Exception {
        String commentline = "# NONSENSEID= 24SFS2";
        Matcher mc = RegexParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());

        commentline = "   # NONSENSEID= 24SFS2  ";
        mc = RegexParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());

        commentline = "# NONSENSEID= 24SFS2  \n";
        mc = RegexParser.getCommentPrefix().matcher(commentline);
        assertTrue(mc.matches());
    }

    @Test
    public void testHeaderPatternOnlyStar() throws Exception {
        Pattern p = RegexParser.getHeaderPattern();
        Matcher m = p.matcher("*");
        assertTrue(m.matches());
        assertEquals("wrong level", "*", m.group(RegexParser.HEADER_STARS_GROUP));
        assertNull("wrong todo", m.group(RegexParser.HEADER_TODO_GROUP));
        assertNull("wrong rest", m.group(RegexParser.HEADER_REST_GROUP));
    }

    @Test
    public void testHeaderPatternOnlyTodo() throws Exception {
        Pattern p = RegexParser.getHeaderPattern("BOB");
        Matcher m = p.matcher("* BOB");
        assertTrue(m.matches());
        assertEquals("wrong level", "*", m.group(RegexParser.HEADER_STARS_GROUP));
        assertEquals("wrong todo", "BOB", m.group(RegexParser.HEADER_TODO_GROUP));
        assertNull("wrong rest", m.group(RegexParser.HEADER_REST_GROUP));
    }

    @Test
    public void testHeaderPatternOnlyTodoWithSpace() throws Exception {
        Pattern p = RegexParser.getHeaderPattern("BOB");
        Matcher m = p.matcher("* BOB ");
        assertTrue(m.matches());
        assertEquals("wrong level", "*", m.group(RegexParser.HEADER_STARS_GROUP));
        assertEquals("wrong todo", "BOB", m.group(RegexParser.HEADER_TODO_GROUP));
        assertEquals("wrong rest", "", m.group(RegexParser.HEADER_REST_GROUP));
    }

    @Test
    public void testHeaderPatternOnlyTodoAndSomething() throws Exception {
        Pattern p = RegexParser.getHeaderPattern("BOB");
        Matcher m = p.matcher("* BOB title :tag1:tag2:");
        assertTrue(m.matches());
        assertEquals("wrong level", "*", m.group(RegexParser.HEADER_STARS_GROUP));
        assertEquals("wrong todo", "BOB", m.group(RegexParser.HEADER_TODO_GROUP));
        assertEquals("wrong rest", "title :tag1:tag2:", m.group(RegexParser.HEADER_REST_GROUP));
    }

    @Test
    public void testHeaderRestPatternOnlyTitle() throws Exception {
        Pattern p = RegexParser.getHeaderRestPattern();
        Matcher m = p.matcher("title");
        assertTrue(m.matches());
        assertEquals("wrong title", "title", m.group(RegexParser.HEADER_REST_TITLE_GROUP));
        assertNull("wrong tags", m.group(RegexParser.HEADER_REST_TAGS_GROUP));
    }

    @Test
    public void testHeaderRestPatternOnlyTags() throws Exception {
        Pattern p = RegexParser.getHeaderRestPattern();
        Matcher m = p.matcher(reverse(":ab:cd:  "));
        assertTrue(m.matches());
        assertEquals("wrong title", "", m.group(RegexParser.HEADER_REST_TITLE_GROUP));
        assertEquals("wrong tags", ":dc:ba:", m.group(RegexParser.HEADER_REST_TAGS_GROUP));
    }

    @Test
    public void testHeaderRestPatternTitleAndTags() throws Exception {
        Pattern p = RegexParser.getHeaderRestPattern();
        Matcher m = p.matcher(reverse(" title here  :tag1:tag2:  "));
        assertTrue(m.matches());
        assertEquals("wrong todo", " title here ", reverse(m.group(RegexParser.HEADER_REST_TITLE_GROUP)));
        assertEquals("wrong tags", ":tag1:tag2:", reverse(m.group(RegexParser.HEADER_REST_TAGS_GROUP)));
    }
}