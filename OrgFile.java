import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Stack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgFile extends OrgNode {
  // Parse the org file
  public static OrgFile createFrom(final String filename)
    throws FileNotFoundException, IOException {

    // Need these to handle file
    String line = null;
    BufferedReader br = null;
    // Need these to handle org parsing
    final Pattern pattern = OrgParser.getHeaderPattern();
    final OrgFile orgfile = new OrgFile(filename);
    final Stack<OrgNode> stack = new Stack<OrgNode>();
    stack.push(orgfile);

    try {
      br = new BufferedReader(new FileReader(filename));
      while ((line = br.readLine()) != null) {

        // See what we are reading
        final Matcher m = pattern.matcher(line);
        if (m.matches()) {
          // Header of node
          // Create new node
          OrgNode node = new OrgNode();
          node.level = m.group("stars").length();
          node.title = m.group("title");
          node.todo = m.group("todo");
          node.tags = OrgParser.parseTags(m.group("tags"));

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
          // Body of node - might be placed in file
          stack.peek().body += line;
        }

      }
    } finally {
      if (br != null) br.close();
    }

    return null;
  }

  // File where this lives
  String filename;

  public OrgFile(final String fname) {
    filename = fname;
  }
}
