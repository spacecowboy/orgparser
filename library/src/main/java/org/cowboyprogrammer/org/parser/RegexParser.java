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

package org.cowboyprogrammer.org.parser;

import org.cowboyprogrammer.org.OrgNode;
import org.cowboyprogrammer.org.OrgTimestamp;
import org.cowboyprogrammer.org.OrgTimestampRange;

import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.cowboyprogrammer.org.util.StringUtils.reverse;


public class RegexParser implements OrgParser {

    /*
     * Can't use named groups because they are not supported in Android.
     */
    public static final int HEADER_STARS_GROUP = 1;
    public static final int HEADER_TODO_GROUP = 2;
    public static final int HEADER_REST_GROUP = 3;

    public static final int HEADER_REST_TAGS_GROUP = 1;
    public static final int HEADER_REST_TITLE_GROUP = 2;

    public static final int TIMESTAMP_TYPE_GROUP = 1;
    public static final int TIMESTAMP_ACTIVE_GROUP = 2;
    public static final int TIMESTAMP_DATE_GROUP = 3;
    public static final int TIMESTAMP_DAY_GROUP = 4;
    public static final int TIMESTAMP_TIME_GROUP = 5;
    public static final int TIMESTAMP_TIMEEND_GROUP = 6;
    public static final int TIMESTAMP_REPEAT_GROUP = 7;
    public static final int TIMESTAMP_WARNING_GROUP = 8;

    public static final int TIMESTAMPRANGE_STARTDATE_GROUP = 1;
    public static final int TIMESTAMPRANGE_STARTDAY_GROUP = 2;
    public static final int TIMESTAMPRANGE_STARTTIME_GROUP = 3;
    public static final int TIMESTAMPRANGE_ENDDATE_GROUP = 4;
    public static final int TIMESTAMPRANGE_ENDDAY_GROUP = 5;
    public static final int TIMESTAMPRANGE_ENDTIME_GROUP = 6;
    private final Pattern headerPattern;
    private final Pattern timestampPattern;
    private final Pattern timestampRangePattern;
    private final Pattern commentPattern;
    private final Pattern headerRestPattern;

    /**
     * Get a regular expression pattern that includes all the possible
     * todo keywords. Only the leading star and todo keyword is placed in groups.
     * The rest have to be processed by getHeaderRestPattern
     */
    public static Pattern getHeaderPattern(final String... todoKeys) {
        final StringBuilder sb = new StringBuilder();
        sb.append("^(\\*+)"); // Leading stars
        sb.append("(?:\\s+(TODO|DONE"); // TODO and DONE hardcoded
        for (final String key : todoKeys) {
            if (key.isEmpty()) continue;
            // Enforce upper case for keys
            sb.append("|").append(key.toUpperCase()); // Add this key
        }
        sb.append("))?");
        //sb.append("(?<prio>\\s+\\[#[A-C]\\])?"); // Optional priority
        sb.append("(?:\\s(.*))?"); // Rest (title and tags)
        sb.append("$"); // End of line
        return Pattern.compile(sb.toString());
    }

    /**
     * Parses the rest of the string in the header, which will be optional title and optional tags.
     * Note, this returns a REVERSED pattern. You have to reverse your string before to match correctly.
     * I.e., "Title :tag:tag:" --> ":gat:gat: eltiT"
     *
     * Once you've matched, reverse the groups again.
     */
    public static Pattern getHeaderRestPattern() {
        final StringBuilder sb = new StringBuilder();
        sb.append("^"); // start of reversed line
        sb.append("\\s*(?:(:.+:))?"); // optional tags
        sb.append("(?:\\s?(.*))?");// optional title
        sb.append("$"); // End of reversed lineline
        return Pattern.compile(sb.toString());
    }

    /**
     * Returns a pattern that will match timestamps. Resulting groups
     * are in order of appearance (only date is mandatory):
     * <p/>
     * date, day, time, timeend, repeat, warning
     * <p/>
     * Example containing all elements:
     * <p/>
     * <2013-12-31 Tue 12:21-14:59 +1w -2d>
     * <p/>
     * In this case, the following would be the matched groups:
     * <p/>
     * date = 2013-12-31
     * day = Tue
     * time = 12:21
     * timeend = 14:59
     * repeat = +1w
     * warning = -2d
     */
    public static Pattern getTimestampPattern() {
        final StringBuilder sb = new StringBuilder();
        // start of line
        sb.append("^")
                // Optional type
                .append("(?:(SCHEDULED|DEADLINE):)?")
                        // Start of date
                .append("\\s*([\\[<])")
                        // Mandatory date
                .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
                        // Optional start
                .append("(?:")
                        // day, not number or space
                .append("(?:\\s+([^\\d\\s]+))?")
                        // time (optional duration)
                .append("(?:\\s+(\\d\\d:\\d\\d)(?:-(\\d\\d:\\d\\d))?)?")
                        // repeater
                .append("(?:\\s+([\\.\\+]?\\+\\d+[hdwmy]))?")
                        // warning
                .append("(?:\\s+(-\\d+[dwmy]))?")
                        // Optional end
                .append(")?")
                        // End
                .append("[\\]>]\\s*$");

        return Pattern.compile(sb.toString());
    }

    /**
     * For timestamps such as:
     * <2014-01-28>--<2014-02-28>
     */
    public static Pattern getTimestampRangePattern() {
        final StringBuilder sb = new StringBuilder();
        // Start
        sb.append("^\\s*[<]")
                // Mandatory date
                .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
                        // Optional start
                .append("(?:")
                        // day, not number or space
                .append("(?:\\s+([^\\d\\s]+))?")
                        // time
                .append("(?:\\s+(\\d\\d:\\d\\d))?")
                        // Optional end
                .append(")?")
                        // End
                .append("[>]")
                        // Range
                .append("--")
                        // Start2
                .append("[<]")
                        // Mandatory date
                .append("(\\d\\d\\d\\d-\\d\\d-\\d\\d)")
                        // Optional start
                .append("(?:")
                        // day, not number or space
                .append("(?:\\s+([^\\d\\s]+))?")
                        // time
                .append("(?:\\s+(\\d\\d:\\d\\d))?")
                        // Optional end
                .append(")?")
                        // End
                .append("[>]\\s*$");

        return Pattern.compile(sb.toString());
    }

    public static Pattern getCommentPrefix() {
        return Pattern.compile("^\\s*#.*\\s*$");
    }


    /**
     * Given a tag-string like ':bob:alice:frank:', returns
     * a list splitted on :, e.g. [bob, alice, frank].
     * <p/>
     * Leading/ending spaces are OK.
     */
    public static String[] parseTags(final String tagString) {
        if (tagString == null) return null;

        final String tags = tagString.trim();
        if (!tags.startsWith(":") || !tags.endsWith(":")) {
            throw new InvalidParameterException("Tag string must" +
                    " start/end with ':'");
        }

        return tags.substring(1).split(":");
    }

    public RegexParser(final String... todoKeys) {
        headerPattern = getHeaderPattern(todoKeys);
        headerRestPattern = getHeaderRestPattern();
        timestampPattern = getTimestampPattern();
        timestampRangePattern = getTimestampRangePattern();
        commentPattern = getCommentPrefix();
    }

    @Override
    public boolean isHeaderLine(String line) {
        return headerPattern.matcher(line).matches();
    }

    /**
     * @param line to parse
     * @return OrgNode with parsed values from line. Will have an empty body.
     */
    @Override
    public OrgNode createFromHeader(String line) {
        final OrgNode node = new OrgNode(this);

        Matcher m = headerPattern.matcher(line);

        if (!m.matches()) {
            throw new IllegalArgumentException("String is not of proper format!");
        }

        node.setLevel(m.group(HEADER_STARS_GROUP).length());
        node.setTodo(m.group(HEADER_TODO_GROUP));

        String rest = m.group(HEADER_REST_GROUP);
        if (rest != null && !rest.isEmpty()) {
            m = headerRestPattern.matcher(reverse(rest));

            if (m.matches()) {
                node.setTitle(reverse(m.group(HEADER_REST_TITLE_GROUP)));
                node.addTags(parseTags(reverse(m.group(HEADER_REST_TAGS_GROUP))));
            }
        }
        return node;
    }

    /**
     * @param line to parse
     * @return true if line is a comment, false otherwise
     */
    @Override
    public boolean isCommentLine(String line) {
        return commentPattern.matcher(line).matches();
    }

    /**
     * @param line to parse
     * @return true if line is a timestamp, false otherwise
     */
    @Override
    public boolean isTimestampLine(String line) {
        return timestampPattern.matcher(line).matches();
    }

    /**
     * @param line to parse
     * @return true if line is a timestamp range, false otherwise
     */
    @Override
    public boolean isTimestampRangeLine(String line) {
        return timestampRangePattern.matcher(line).matches();
    }

    /**
     * @param line to parse
     * @return a parsed OrgTimestamp
     */
    @Override
    public OrgTimestamp getTimestamp(String line) {
        final Matcher m = timestampPattern.matcher(line);
        if (!m.matches()) {
            throw new IllegalArgumentException("String is not of proper format!");
        }
        return new OrgTimestamp(m.group(RegexParser.TIMESTAMP_ACTIVE_GROUP),
                m.group(RegexParser.TIMESTAMP_TYPE_GROUP),
                m.group(RegexParser.TIMESTAMP_DATE_GROUP),
                m.group(RegexParser.TIMESTAMP_TIME_GROUP),
                m.group(RegexParser.TIMESTAMP_TIMEEND_GROUP),
                m.group(RegexParser.TIMESTAMP_WARNING_GROUP),
                m.group(RegexParser.TIMESTAMP_REPEAT_GROUP));
    }

    /**
     * @param line to parse
     * @return a parsed OrgTimestampRange
     */
    @Override
    public OrgTimestampRange getTimestampRange(String line) {
        final Matcher m = timestampRangePattern.matcher(line);
        if (!m.matches()) {
            throw new IllegalArgumentException("String is not of proper format!");
        }
        return new OrgTimestampRange(m.group(RegexParser.TIMESTAMPRANGE_STARTDATE_GROUP),
                m.group(RegexParser.TIMESTAMPRANGE_ENDDATE_GROUP),
                m.group(RegexParser.TIMESTAMPRANGE_STARTTIME_GROUP),
                m.group(RegexParser.TIMESTAMPRANGE_ENDTIME_GROUP));
    }
}
