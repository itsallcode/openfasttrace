package org.itsallcode.openfasttrace.importer.markdown;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LinePattern;

class MdSectionTitlePattern implements LinePattern
{
    private static final Pattern UNDERLINE = MdPattern.UNDERLINE.getPattern();
    private static final Pattern HASH_TITLE = MdPattern.TITLE.getPattern();

    @Override
    public Pattern getPattern()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getPattern()'. Use 'getMatches()' instead.");
    }

    @Override
    public Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        final Matcher hashTitleMatcher = HASH_TITLE.matcher(line);
        if (hashTitleMatcher.matches())
        {
            return Optional.of(List.of(hashTitleMatcher.group(1)));
        }
        if (line != null && nextLine != null && UNDERLINE.matcher(nextLine).matches())
        {
            return Optional.of(List.of(line));
        }
        return Optional.empty();
    }
}
