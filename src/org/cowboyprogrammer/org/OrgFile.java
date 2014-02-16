package org.cowboyprogrammer.org;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Stack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgFile extends OrgNode {
  /**
   * Parse a specific org file and return a recursive structure of
   * orgnodes.
   */
  public static OrgFile createFrom(final String filename)
    throws FileNotFoundException, IOException {

    // Need these to handle file
    String line = null;
    BufferedReader br = null;
    // Need these to handle org parsing
    final Pattern pattern = OrgParser.getHeaderPattern();
    final OrgFile orgfile = new OrgFile(filename);
    final Stack<OrgNode> stack = new Stack<OrgNode>();
    // Root is file
    stack.push(orgfile);

    try {
      br = new BufferedReader(new FileReader(filename));
      while ((line = br.readLine()) != null) {

        // See what we are reading
        final Matcher m = pattern.matcher(line);
        if (m.matches()) {
          // Header of node
          // Create new node
          final OrgNode node = new OrgNode();
          node.level = m.group("stars").length();
          node.title = m.group("title");
          node.todo = m.group("todo");
          node.addTags(OrgParser.parseTags(m.group("tags")));

          // Find parent
          while (node.level <= stack.peek().level) {
            stack.pop();
          }

          // Assign parent
          node.parent = stack.peek();
          // Assign child
          stack.peek().subNodes.add(node);
          // Add to stack
          stack.push(node);

        } else {
          // Body of node - OK to place in file
          stack.peek().body += line + "\n";
        }

      }
    } finally {
      if (br != null) br.close();
    }

    return orgfile;
  }

  // File where this lives
  String filename;

  public OrgFile(final String fname) {
    filename = fname;
  }

  public void writeToFile() throws IOException {
    writeToFile(this.filename);
  }

  public void writeToFile(final String filename) throws IOException {

    BufferedWriter bw = null;
    try {
      final File file = new File(filename);
      if (!file.exists()) file.createNewFile();

      bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      // Write the org tree
      bw.write(this.treeToString());

    } finally {
      if (bw != null) bw.close();
    }
  }
}
