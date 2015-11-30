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

package org.cowboyprogrammer.org;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class OrgParserTest {
    @Test
    public void testParsingBody() throws Exception {
        // Check if body grows
        final String orgBody = "Body of two lines with\nno ending newline";
        final String orgEntry = "* Simple header\n" + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        assertEquals(orgBody + "\n", orgFile1.getSubNodes().get(0).getBody());
    }

    @Test
    public void testParsingBodies() throws Exception {
        // Check if body grows
        final String orgHeader = "* Simple header\n";
        final String orgBody = "Body of two lines with\nending new lines\n";
        final String orgEntry = orgHeader + orgBody + orgHeader +
                orgBody;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        assertEquals(orgBody, orgFile1.getSubNodes().get(0).getBody());
        assertEquals(orgBody, orgFile1.getSubNodes().get(1).getBody());
    }

    @Test
    public void testParagraphs() throws Exception {
        // Maintain paragraph formatting
        final String orgHeader = "* Simple header\n";
        final String orgBody = "This is a simple paragraph.\n\nA paragraph is separated by " +
                "atleast two spaces.\n\n\nThis is separated by three and ends with two, with " +
                "space.\n \n";
        final String orgEntry = orgHeader + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        assertEquals(orgBody, orgFile1.getSubNodes().get(0).getBody());

    }

    @Test
    public void testCommentParagraphGrowth() throws Exception {
        OrgNode node = new OrgNode();
        // Maintain paragraph formatting
        final String orgHeader = "* Simple header\n";
        final String orgBody = "This is a simple paragraph.\n\nA paragraph is separated by " +
                "atleast two spaces.\n\n\nThis is separated by three and ends with two, with " +
                "space.\n \n";
        final String commentline = "# NONSENSEID= 24SFS2\n";

        final String orgEntry = orgHeader + commentline + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString("test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString("test.org", orgFile2.treeToString());
        assertEquals(orgBody, orgFile3.getSubNodes().get(0).getBody());
        assertEquals(commentline, orgFile3.getSubNodes().get(0).getComments());

    }

    @Test
    public void testRepeatedParsedBodies() throws Exception {
        // Check if body grows
        final String orgBody = "A simple body\nConsisting of a few paragraphs\n\nThe third of " +
                "which, is separated by three newlines\n\n\nAnd ends with just one.\n";
        final String orgEntry = "* Simple header\n" + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString("test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString("test.org", orgFile2.treeToString());
        assertEquals(orgBody, orgFile3.getSubNodes().get(0).getBody());

    }

    @Test
    public void testItemGrowth() throws Exception {
        // Check if body grows
        final String h1 = "* Header one\n";
        final String b1 = "Body of\nitem one should not\ngrow\n";
        final String h2 = "* Header two\n";
        final String b2 = "Body of\nitem two should not\ngrow either\n";
        final String orgEntry = h1 + b1 + "\n" + h2 + b2;

        OrgFile orgFile1 = OrgFile.createFromString("test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString("test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString("test.org", orgFile2.treeToString());
        assertEquals(h1, orgFile3.getSubNodes().get(0).getOrgHeader() + "\n");
        assertEquals(b1, orgFile3.getSubNodes().get(0).getBody());
        assertEquals(h2, orgFile3.getSubNodes().get(1).getOrgHeader() + "\n");
        assertEquals(b2, orgFile3.getSubNodes().get(1).getBody());

    }

    @Test
    public void testCommentPrefix() throws Exception {
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
    public void testComments() throws Exception {
        OrgNode node = new OrgNode();
        final String commentline = "# NONSENSEID= 24SFS2";
        final String normalline = "Bob bob";

        node.addBodyLine(commentline);
        node.addBodyLine(normalline);

        assertEquals(commentline + "\n", node.getComments());
        assertEquals(normalline + "\n", node.getBody());


    }

    @Test
    public void testDefaultHeaderPattern() throws Exception {
        Pattern p = OrgParser.getHeaderPattern();
        Matcher m = p.matcher("* BOB A simple title :bob:alice:");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.HEADER_STARS_GROUP), "*");
        assertEquals(m.group(OrgParser.HEADER_TITLE_GROUP), "BOB A simple title");
        assertNull(m.group(OrgParser.HEADER_TODO_GROUP));
        assertEquals(m.group(OrgParser.HEADER_TAGS_GROUP), ":bob:alice:");
    }

    @Test
    public void testHeaderPatternTODO() throws Exception {
        Pattern p = OrgParser.getHeaderPattern("BOB");
        Matcher m = p.matcher("* BOB A simple title :bob:alice:");
        assertTrue(m.matches());
        assertEquals(m.group(OrgParser.HEADER_STARS_GROUP), "*");
        assertEquals(m.group(OrgParser.HEADER_TITLE_GROUP), "A simple title");
        assertEquals(m.group(OrgParser.HEADER_TODO_GROUP), "BOB");
        assertEquals(m.group(OrgParser.HEADER_TAGS_GROUP), ":bob:alice:");
    }
}