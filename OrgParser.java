import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OrgParser {

  /**
   * Get a regular expression pattern that includes all the possible
   * todo keywords. Matches are placed in named groups:
   * stars, todo, title, tags
   */
  public static Pattern getHeaderPattern(final String... todoKeys) {
    final StringBuilder sb = new StringBuilder();
    sb.append("^(?<stars>\\*+)"); // Leading stars
    sb.append("(\\s+(?<todo>TODO|DONE"); // TODO and DONE hardcoded
    for (final String key: todoKeys) {
      if (key.isEmpty()) continue;
      // Enforce upper case for keys
      sb.append("|").append(key.toUpperCase()); // Add this key
    }
    sb.append("))?"); // TODO keys are optional
    //sb.append("(?<prio>\\s+\\[#[A-C]\\])?"); // Optional priority
    sb.append("\\s+(?<title>.+?)"); // Title
    sb.append("(\\s+(?<tags>:.+:))?"); // Optional Tags
    sb.append("\\s*$"); // End of line
    return Pattern.compile(sb.toString());
  }

  /**
   * Given a tag-string like ':bob:alice:frank:', returns
   * a list splitted on :, e.g. [bob, alice, frank].
   *
   * Leading/ending spaces are OK.
   */
  public static String[] parseTags(final String otags) {
    if (otags == null) return null;

    final String tags = otags.trim();
    if (!tags.startsWith(":") || !tags.endsWith(":")) {
      throw new InvalidParameterException("Tag string must" +
                                          " start/end with ':'");
    }

    return tags.substring(1).split(":");
  }

}
