package org.cowboyprogrammer.org;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import java.util.Stack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgFile extends OrgNode {
  
  /**
   * Parse the content of an org-mode file and return a recursive structure of orgnodes.
   * @throws ParseException 
   */
  public static OrgFile createFromString(final String filename,
      final String content) throws ParseException {
    // Need these to handle org parsing
    final Pattern pattern = OrgParser.getHeaderPattern();
    final OrgFile orgfile = new OrgFile(filename);
    final Stack<OrgNode> stack = new Stack<OrgNode>();
    // Root is file
    stack.push(orgfile);

    for (String line : content.split("\\r?\\n|\\r")) {
      // See what we are reading
      final Matcher m = pattern.matcher(line);
      if (m.matches()) {
        // Header of node
        // Create new node
        final OrgNode node = new OrgNode();
        node.setLevel(m.group(OrgParser.HEADER_STARS_GROUP).length());
        node.setTitle(m.group(OrgParser.HEADER_TITLE_GROUP));
        node.setTodo(m.group(OrgParser.HEADER_TODO_GROUP));
        node.addTags(OrgParser.parseTags(m.group(OrgParser.HEADER_TAGS_GROUP)));

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

      } else {
        // Body of node - OK to place in file
        stack.peek().addBodyLine(line + "\n");
      }
    }

    return orgfile;
  }
  
  /**
   * Parse a specific org file and return a recursive structure of orgnodes.
   * @throws IOException 
   * @throws ParseException 
   */
  public static OrgFile createFromFile(final File file) throws IOException, ParseException {
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();
    try {
      br = new BufferedReader(new FileReader(file));
      
      sb.append(br.readLine()).append("\n");
    } finally {
      if (br != null)
        br.close();
    }
    
    return createFromString(file.getPath(), sb.toString());
  }

  /**
   * Parse a specific org file and return a recursive structure of orgnodes.
   */
  public static OrgFile createFromFile(final String filepath)
      throws FileNotFoundException, IOException, ParseException {
    
    return createFromFile(new File(filepath));
  }

  // File where this lives
  private String filename;

  public OrgFile(final String fname) {
    filename = fname;
  }

  /**
   * Last modified time of the parsed file. Only valid for existing files,
   * else -1.
   */
  public long lastModified() {
    File f = new File(filename);
    if (f.exists())
      return f.lastModified();
    else
      return -1;
  }

  public String getFilename() {
    return filename;
  }

  public void writeToFile() throws IOException {
    writeToFile(this.filename);
  }

  public void writeToFile(final String filename) throws IOException {

    BufferedWriter bw = null;
    try {
      final File file = new File(filename);
      if (!file.exists())
        file.createNewFile();

      bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
      // Write the org tree
      bw.write(this.treeToString());

    } finally {
      if (bw != null)
        bw.close();
    }
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
