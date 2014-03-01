package org.cowboyprogrammer.org;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class OrgNode {
  public static final Pattern timestampPattern = OrgParser
      .getTimestampPattern();
  public static final Pattern timestampRangePattern = OrgParser
      .getTimestampRangePattern();
  // Parent node of this node
  private OrgNode parent = null;
  // A heading can have any number of sub-headings
  private final List<OrgNode> subNodes;
  // Tags defined on this node
  private final List<String> tags;
  // Timestamps associated with entry
  private final List<OrgTimestamp> timestamps;
  private final List<OrgTimestampRange> timestampRanges;
  // Heading level (number of stars). Must be greater than parent.
  // 0 only valid for file object
  private int level = 0;
  // TODO keyword
  private String todo = null;
  // Title of heading (includes anything that was not parsed)
  private String title = "";
  // Body of entry
  private String body = "";
  // Comments before body
  private String comments = "";

  public OrgNode() {
    timestamps = new ArrayList<OrgTimestamp>();
    timestampRanges = new ArrayList<OrgTimestampRange>();
    subNodes = new ArrayList<OrgNode>();
    tags = new ArrayList<String>();
  }

  /**
   * Add all tags.
   */
  public void addTags(final String... tags) {
    for (final String tag : tags) {
      this.tags.add(tag);
    }
  }

  /**
   * Add a line to this entry's body. It is parsed and converted to timestamp
   * etc.
   */
  public void addBodyLine(final String line) throws ParseException {
    // If empty, then we can add timestamps and comments
    if (body.isEmpty() || body.matches("\\A\\s*\\z")) {
      // Check if comment
      if (line.startsWith("#")) {
        // It's a comment
        comments += line;
        return;
      }
      final Matcher mt = timestampPattern.matcher(line);
      if (mt.matches()) {
        // Don't keep spaces before timestamps
        body = "";
        timestamps.add(new OrgTimestamp(mt));
        return;
      } 
      final Matcher mr = timestampRangePattern.matcher(line);
      if (mr.matches()) {
        // Don't keep spaces before timestamps
        body = "";
        timestampRanges.add(new OrgTimestampRange(mr));
        return;
      }
    }
    // Nothing happened above, just add to body
    body += line;
  }

  /**
   * Get body of this entry for org-mode.
   */
  public String getOrgBody() {
    final StringBuilder sb = new StringBuilder();
    
    sb.append(this.comments);
    
    for (OrgTimestamp t : timestamps) {
      sb.append(t.toString()).append("\n");
    }
    
    for (OrgTimestampRange t : timestampRanges) {
      sb.append(t.toString()).append("\n");
    }

    sb.append(this.body);
    
    return sb.toString();
  }

  /**
   * Get the header of this entry for org-mode.
   */
  public String getOrgHeader() {
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
    if (getLevel() < 1) {
      return;
    }

    for (int i = 0; i < getLevel(); i++) {
      sb.append("*");
    }
    sb.append(" ");
    if (this.todo != null) {
      sb.append(this.todo).append(" ");
    }
    sb.append(this.title);
    if (!this.tags.isEmpty()) {
      sb.append(" :");
      for (final String tag : this.tags) {
        sb.append(tag).append(":");
      }
    }
    sb.append("\n");
  }

  /**
   * The String representation of this specific entry.
   */
  public String toString() {
    return getOrgHeader() + getOrgBody();
  }

  /**
   * Append the String representation of this specific entry.
   */
  protected void toString(final StringBuilder sb) {
    getHeaderString(sb);
    sb.append(getOrgBody());
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
    for (final OrgNode child : this.subNodes) {
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

  public List<OrgNode> getSubNodes() {
    return subNodes;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<OrgTimestamp> getTimestamps() {
    return timestamps;
  }

  public void addTimestamp(final OrgTimestamp... timestamps) {
    for (final OrgTimestamp ts : timestamps) {
      this.timestamps.add(ts);
    }
  }

  public List<OrgTimestampRange> getTimestampRanges() {
    return timestampRanges;
  }

  public void addTimestampRange(final OrgTimestampRange... timestamps) {
    for (final OrgTimestampRange tr : timestamps) {
      this.timestampRanges.add(tr);
    }
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(final int level) {
    if (level < 0) {
      throw new IllegalArgumentException(
          "Level not allowed to be negative. Only a file can be level 0.");
    }
    this.level = level;
  }

  public String getTodo() {
    return todo;
  }

  public void setTodo(final String todo) {
    this.todo = todo;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    if (title == null) {
      throw new NullPointerException("Not allowed to be null!");
    }
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  /**
   * Set the body of this entry. Note that this is not parsed and
   * does not modify existing timestamps etc in this object.
   */
  public void setBody(final String body) {
    if (body == null) {
      throw new NullPointerException("Not allowed to be null!");
    }
    this.body = body;
  }

  public OrgNode getParent() {
    return parent;
  }

  public void setParent(final OrgNode parent) {
    if (parent.getLevel() >= this.level) {
      throw new IllegalArgumentException(
          "Parent's level must be less than this entry's level!");
    }

    this.parent = parent;
  }

  public String getComments() {
    return comments;
  }

  /**
   * Please note that this function does not modify any other fields.
   */
  public void setComments(final String comments) {
    if (comments == null) {
      throw new NullPointerException("Not allowed to be null!");
    }
    this.comments = comments;
  }
}
