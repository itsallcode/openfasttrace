package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleLinePattern implements LinePattern
{
    private final Pattern pattern;

    private SimpleLinePattern(final Pattern pattern)
    {
        this.pattern = pattern;
    }

    public static SimpleLinePattern of(final String pattern)
    {
        return new SimpleLinePattern(Pattern.compile(pattern));
    }

    public Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        final Matcher matcher = pattern.matcher(line);
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
