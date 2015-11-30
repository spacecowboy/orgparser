package org.cowboyprogrammer.org.parser;


import org.cowboyprogrammer.org.OrgNode;
import org.cowboyprogrammer.org.OrgTimestamp;
import org.cowboyprogrammer.org.OrgTimestampRange;

public interface OrgParser {

    /**
     * @param line to parse
     * @return true if the line is the header of an OrgNode, like "* TODO title :tag1:tag2:"
     */
    boolean isHeaderLine(String line);

    /**
     * @param line to parse
     * @return OrgNode with parsed values from line. Will have an empty body.
     */
    OrgNode createFromHeader(String line);

    /**
     *
     * @param line to parse
     * @return true if line is a comment, false otherwise
     */
    boolean isCommentLine(String line);

    /**
     *
     * @param line to parse
     * @return true if line is a timestamp, false otherwise
     */
    boolean isTimestampLine(String line);

    /**
     *
     * @param line to parse
     * @return true if line is a timestamp range, false otherwise
     */
    boolean isTimestampRangeLine(String line);

    /**
     *
     * @param line to parse
     * @return a parsed OrgTimestamp
     */
    OrgTimestamp getTimestamp(String line);

    /**
     *
     * @param line to parse
     * @return a parsed OrgTimestampRange
     */
    OrgTimestampRange getTimestampRange(String line);
}
