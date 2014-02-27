package org.cowboyprogrammer.org;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class OrgNode {
  protected static final Pattern timestampPattern =
    OrgParser.getTimestampPattern();
  // Parent node of this node
  public OrgNode parent = null;
  // A heading can have any number of sub-headings
  public final List<OrgNode> subNodes;
  // Tags defined on this node
  public final List<String> tags;
  // Timestamps associated with entry
  public List<OrgTimestamp> timestamps;
  // Heading level (number of stars). Must be greater than parent.
  // 0 only valid for file object
  public int level = 0;
  // TODO keyword
  public String todo = null;
  // Title of heading (includes anything that was not parsed)
  public String title = "";
  // Body of entry
  protected String body = "";

  public OrgNode() {
    timestamps = new ArrayList<OrgTimestamp>();
    subNodes = new ArrayList<OrgNode>();
    tags = new ArrayList<String>();
  }

  /**
   * Add all tags.
   */
  public void addTags(final String... tags) {
    if (tags == null) return;

    for (final String tag: tags) {
      this.tags.add(tag);
    }
  }

  public void addBodyLine(final String line) throws ParseException {
    // If empty, then we can add timestamps
    if (body.isEmpty() || body.matches("\\A\\s*\\z")) {
      final Matcher m = timestampPattern.matcher(line);
      if (m.matches()) {
        System.out.println("Timestamp!");
        // Don't keep spaces before timestamps
        body = "";
        timestamps.add(new OrgTimestamp(m));
      } else {
        // Just append
        body += line;
      }
    } else {
      body += line;
    }
  }

  /**
   * Get body of this entry.
   */
  public String getBodyString() {
    return this.body;
  }

  /**
   * Get the header of this entry.
   */
  public String getHeaderString() {
    final StringBuilder sb = new StringBuilder();
    getHeaderString(sb);
    // Remove ending newline
    return sb.toString().trim();
  }

  /**
   * Append the header of this entry.
   * Will end with newline.
   */
  protected void getHeaderString(final StringBuilder sb) {
    // No header without stars
    if (level < 1) return;

    for (int i = 0; i < level; i++) {
      sb.append("*");
    }
    sb.append(" ");
    if (this.todo != null) {
      sb.append(this.todo).append(" ");
    }
    sb.append(this.title);
    if (!this.tags.isEmpty()) {
      sb.append(" :");
      for (final String tag: this.tags) {
        sb.append(tag).append(":");
      }
    }
    sb.append("\n");
  }

  /**
   * The String representation of this specific entry.
   */
  public String toString() {
    return getHeaderString() + getBodyString();
  }

  /**
   * Append the String representation of this specific entry.
   */
  protected void toString(final StringBuilder sb) {
    getHeaderString(sb);
    sb.append(getBodyString());
  }

  /**
   * Get a String representation of the entire sub tree including
   * this.
   */
  public String treeToString() {
    final StringBuilder sb = new StringBuilder();
    treeToString(sb);
    return sb.toString();
  }

  /**
   * Append a String representation of the entire sub tree including
   * this.
   */
  protected void treeToString(final StringBuilder sb) {
    this.toString(sb);
    for (final OrgNode child: this.subNodes) {
      child.treeToString(sb);
    }
  }

  /**
   * Tags defined on this node AND any parents.
   */
  public List<String> getAllTags() {
    final List<String> tags = new ArrayList<String>();
    // Add my tags
    tags.addAll(this.tags);
    // And parents.
    OrgNode ancestor = this.parent;
    while (ancestor != null) {
      tags.addAll(ancestor.tags);
      ancestor = ancestor.parent;
    }
    return tags;
  }

}
