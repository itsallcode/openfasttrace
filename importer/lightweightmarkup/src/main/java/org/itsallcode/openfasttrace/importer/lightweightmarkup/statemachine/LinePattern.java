package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common interface for text patterns used in line parsers.
 */
public interface LinePattern
{
    /**
     * Get the regular expression pattern associated with this line pattern.
     *
     * @return regular expression pattern
     */
    Pattern getPattern();

    default Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        final Matcher matcher = getPattern().matcher(line);
        if (matcher.matches())
        {
            final List<String> matches = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++)
            {
                matches.add(matcher.group(i));
            }
            return Optional.of(matches);
        }
        return Optional.empty();
    }
}
