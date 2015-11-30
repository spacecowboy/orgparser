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

package org.cowboyprogrammer.org.parser;

import org.cowboyprogrammer.org.OrgFile;
import org.cowboyprogrammer.org.OrgNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class OrgParserTest {

    private final OrgParser parser;

    @Parameterized.Parameters
    public static Collection<OrgParser> parsersToTest() {
        return Arrays.asList((OrgParser) new RegexParser());
    }

    public OrgParserTest(OrgParser parserToTest) {
        this.parser = parserToTest;
    }

    @Test
    public void testParsingBody() throws Exception {
        // Check if body grows
        final String orgBody = "Body of two lines with\nno ending newline";
        final String orgEntry = "* Simple header\n" + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
        assertEquals(orgBody + "\n", orgFile1.getSubNodes().get(0).getBody());
    }

    @Test
    public void testParsingBodies() throws Exception {
        // Check if body grows
        final String orgHeader = "* Simple header\n";
        final String orgBody = "Body of two lines with\nending new lines\n";
        final String orgEntry = orgHeader + orgBody + orgHeader +
                orgBody;

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
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

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
        assertEquals(orgBody, orgFile1.getSubNodes().get(0).getBody());

    }

    @Test
    public void testCommentParagraphGrowth() throws Exception {
        OrgNode node = new OrgNode(parser);
        // Maintain paragraph formatting
        final String orgHeader = "* Simple header\n";
        final String orgBody = "This is a simple paragraph.\n\nA paragraph is separated by " +
                "atleast two spaces.\n\n\nThis is separated by three and ends with two, with " +
                "space.\n \n";
        final String commentline = "# NONSENSEID= 24SFS2\n";

        final String orgEntry = orgHeader + commentline + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString(parser, "test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString(parser, "test.org", orgFile2.treeToString());
        assertEquals(orgBody, orgFile3.getSubNodes().get(0).getBody());
        assertEquals(commentline, orgFile3.getSubNodes().get(0).getComments());

    }

    @Test
    public void testRepeatedParsedBodies() throws Exception {
        // Check if body grows
        final String orgBody = "A simple body\nConsisting of a few paragraphs\n\nThe third of " +
                "which, is separated by three newlines\n\n\nAnd ends with just one.\n";
        final String orgEntry = "* Simple header\n" + orgBody;

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString(parser, "test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString(parser, "test.org", orgFile2.treeToString());
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

        OrgFile orgFile1 = OrgFile.createFromString(parser, "test.org", orgEntry);
        OrgFile orgFile2 = OrgFile.createFromString(parser, "test.org", orgFile1.treeToString());
        OrgFile orgFile3 = OrgFile.createFromString(parser, "test.org", orgFile2.treeToString());
        assertEquals(h1, orgFile3.getSubNodes().get(0).getOrgHeader() + "\n");
        assertEquals(b1, orgFile3.getSubNodes().get(0).getBody());
        assertEquals(h2, orgFile3.getSubNodes().get(1).getOrgHeader() + "\n");
        assertEquals(b2, orgFile3.getSubNodes().get(1).getBody());
    }

    @Test
    public void testSimpleFullHeader() throws Exception {
        OrgNode node = parser.createFromHeader("* TODO  A simple title  :bob:alice:");

        assertEquals("wrong level", 1, node.getLevel());
        assertEquals("wrong todo", "TODO", node.getTodo());
        assertEquals("wrong title", " A simple title ", node.getTitle());
        assertArrayEquals("wrong tags", new String[]{"bob", "alice"}, node.getTags().toArray());
    }

    @Test
    public void testHeaderTODONoTitleNoTags() throws Exception {
        OrgNode node = parser.createFromHeader("* TODO");

        assertEquals("wrong level", 1, node.getLevel());
        assertEquals("wrong todo", "TODO", node.getTodo());
        assertEquals("wrong title", "", node.getTitle());
        assertTrue("wrong tags", node.getTags().isEmpty());
    }

    @Test
    public void testHeaderTODONoTitleWithTags() throws Exception {
        OrgNode node = parser.createFromHeader("* TODO :bob:alice:");

        assertEquals("wrong level", 1, node.getLevel());
        assertEquals("wrong todo", "TODO", node.getTodo());
        assertEquals("wrong title", "", node.getTitle());
        assertArrayEquals("wrong tags", new String[]{"bob", "alice"}, node.getTags().toArray());
    }

    @Test
    public void testHeaderWithTags() throws Exception {
        OrgNode node = parser.createFromHeader("* :bob:alice:");

        assertEquals("wrong level", 1, node.getLevel());
        assertNull("wrong todo", node.getTodo());
        assertEquals("wrong title", "", node.getTitle());
        assertArrayEquals("wrong tags", new String[]{"bob", "alice"}, node.getTags().toArray());
    }

    @Test
    public void testReadBackWithTitleWithTODO() throws Exception {
        OrgNode node = new OrgNode(parser);
        node.addBodyLine("A simple body");
        node.setLevel(1);
        node.setTodo("TODO");
        node.setTitle("A simple title");

        String s = node.treeToString();

        assertEquals("Wrong string representation", "* TODO A simple title\nA simple body\n", s);

        // Read it back
        OrgFile root = OrgFile.createFromString(parser, "test.org", s);
        OrgNode node2 = root.getSubNodes().get(0);

        assertEquals("Wrong title", node.getTitle(), node2.getTitle());
        assertEquals("Wrong level", node.getLevel(), node2.getLevel());
        assertEquals("Wrong todo", node.getTodo(), node2.getTodo());
        assertEquals("Wrong body", node.getBody(), node2.getBody());

        assertEquals("Wrong string version", s, node2.treeToString());
    }

    @Test
    public void testReadBackEmptyTitleWithTODO() throws Exception {
        // TO-DO could be interpreted as the title, but Emacs treats it as to-do so that's what we should do
        OrgNode node = new OrgNode(parser);
        node.addBodyLine("A simple body");
        node.setLevel(1);
        node.setTodo("TODO");
        node.setTitle("");

        String s = node.treeToString();

        assertEquals("Wrong string representation", "* TODO \nA simple body\n", s);

        // Read it back
        OrgFile root = OrgFile.createFromString(parser, "test.org", s);
        OrgNode node2 = root.getSubNodes().get(0);

        assertEquals("Wrong title", node.getTitle(), node2.getTitle());
        assertEquals("Wrong level", node.getLevel(), node2.getLevel());
        assertEquals("Wrong todo", node.getTodo(), node2.getTodo());
        assertEquals("Wrong body", node.getBody(), node2.getBody());

        assertEquals("Wrong string version", s, node2.treeToString());
    }

    @Test
    public void testComments() throws Exception {
        OrgNode node = new OrgNode(parser);
        final String commentline = "# NONSENSEID= 24SFS2";
        final String normalline = "Bob bob";

        node.addBodyLine(commentline);
        node.addBodyLine(normalline);

        assertEquals(commentline + "\n", node.getComments());
        assertEquals(normalline + "\n", node.getBody());
    }
}