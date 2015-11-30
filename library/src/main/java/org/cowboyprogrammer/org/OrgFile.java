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

import org.cowboyprogrammer.org.parser.OrgParser;

import java.io.*;
import java.text.ParseException;
import java.util.Stack;

public class OrgFile extends OrgNode {

    // File where this lives
    private String filename;

    public OrgFile(final OrgParser orgParser, final String fname) {
        super(orgParser);
        filename = fname;
    }

    /**
     * Read an org file.
     *
     * @param filename
     *         The filename without the path part
     * @param br
     *         A buffered reader of the file contents
     *
     * @return an OrgFile object containing the file's contents
     *
     * @throws ParseException
     * @throws IOException
     */
    public static OrgFile createFromBufferedReader(final OrgParser parser, final String filename,
                                                   final BufferedReader br) throws IOException, ParseException {
        if (null == filename || br == null) {
            throw new NullPointerException("Can't read a null buffer");
        }
        // Need these to handle org parsing
        final OrgFile orgfile = new OrgFile(parser, filename);
        final Stack<OrgNode> stack = new Stack<OrgNode>();
        // Root is file
        stack.push(orgfile);

        String line, sepline = null;

        try {
            while ((line = br.readLine()) != null) {
                // See what we are reading
                if (parser.isHeaderLine(line)) {
                    // Destroy separator line
                    sepline = null;
                    // Header of node
                    // Create new node
                    final OrgNode node = parser.createFromHeader(line);

                    // Find parent
                    while (node.getLevel() <= stack.peek().getLevel()) {
                        stack.pop();
                    }

                    // Assign parent
                    node.setParent(stack.peek());
                    // Assign child
                    stack.peek().getSubNodes().add(node);
                    // Add to stack
                    stack.push(node);
                    /*
                    Sep line handles a possible separator line between the
                    body of the previous item and the header of the next item
                    . One separator line is allowed,
                    and will thus get "eaten" during parsing.
                     */
                } else if (sepline != null && line.isEmpty()) {
                    // Another empty line, put last one in node
                    stack.peek().addBodyLine(sepline);
                    sepline = line;
                } else if (sepline == null && line.isEmpty()) {
                    // Possibly a separator line. Keep track of it.
                    sepline = line;
                } else {
                    // Body of node - OK to place in file
                    // Put sepline there first if not empty
                    if (sepline != null) {
                        stack.peek().addBodyLine(sepline);
                        sepline = null;
                    }
                    stack.peek().addBodyLine(line);
                }
            }
        } finally {
            br.close();
        }

        return orgfile;
    }

    /**
     * Read an org file.
     *
     * @param parser
     *         The OrgParser to use
     * @param filename
     *         The filename without the path part
     * @param content
     *         The file's contents
     *
     * @return an OrgFile object containing the file's contents
     *
     * @throws ParseException
     * @throws IOException
     */
    public static OrgFile createFromString(final OrgParser parser, final String filename, final String content)
            throws ParseException, IOException {
        return createFromBufferedReader(parser, filename, new BufferedReader(new StringReader(content)));
    }

    /**
     * Read an org file.
     *
     * @param parser
     *         The OrgParser to use
     * @param file
     *         The file open and parse
     *
     * @return an OrgFile object containing the file's contents
     *
     * @throws ParseException
     * @throws IOException
     */
    public static OrgFile createFromFile(final OrgParser parser, final File file) throws IOException, ParseException {
        return createFromBufferedReader(parser, file.getName(), new BufferedReader(new FileReader(file)));
    }

    /**
     * Read an org file.
     *
     * @param parser
     *         The OrgParser to use
     * @param filepath
     *         The full path to the file to open and parse.
     *
     * @return an OrgFile object containing the file's contents
     *
     * @throws ParseException
     * @throws IOException
     */
    public static OrgFile createFromFile(final OrgParser parser, final String filepath)
            throws FileNotFoundException, IOException, ParseException {

        return createFromFile(parser, new File(filepath));
    }

    /**
     * Last modified time of the parsed file. Only valid for existing files, else -1.
     */
    public long lastModified() {
        File f = new File(filename);
        if (f.exists()) {
            return f.lastModified();
        } else {
            return -1;
        }
    }

    /**
     * Return the filename without the path part.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the filename without the path part.
     */
    public void setFilename(final String filename) {
        this.filename = filename;
    }

    /**
     * Writes the org tree to the writer. Just a convenience method.
     *
     * @param bw
     *         A bufferedwriter to write to
     *
     * @throws IOException
     */
    public void writeToBuffer(final BufferedWriter bw) throws IOException {
        // Write the org tree
        bw.write(this.treeToString());

    }

    public boolean delete() throws IOException {
        return delete(this.filename);
    }

    public boolean delete(final String filename) throws IOException {
        final File file = new File(filename);
        return file.delete();
    }

    /**
     * Renames the file of this object. Returns true if success.
     */
    public boolean rename(final String newFilename) {
        if (newFilename == null) {
            throw new NullPointerException();
        }
        if (newFilename.equals(filename)) {
            return false;
        }

        final File file = new File(filename);
        final boolean res = file.renameTo(new File(newFilename));

        if (res) {
            this.filename = newFilename;
        }

        return res;
    }
}
