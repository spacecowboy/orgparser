import java.util.List;
import java.util.ArrayList;

public class OrgNode {
  // Parent node of this node
  public OrgNode parent = null;
  // A heading can have any number of sub-headings
  public List<OrgNode> subNodes;
  // Get the String representation of this individual node
  //public String toString();
  // Get a String representation of the entire sub tree including this
  //public String treeToString();
  // Tags defined on this node
  public String[] tags;
  // Tags defined on this node AND any parents
  //public List<String> getAllTags();
  // Heading level (number of stars). Must be greater than parent.
  // 0 only valid for file object
  public int level = 0;
  // TODO keyword
  public String todo = null;
  // Title of heading (includes anything that was not parsed)
  public String title = "";
  // Body of entry
  public String body = "";

  public OrgNode() {
    subNodes = new ArrayList<OrgNode>();
  }
}
