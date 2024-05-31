package org.itsallcode.openfasttrace.importer.restructuredtext;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LinePattern;

class RstSectionTitlePattern implements LinePattern
{
    private static final Pattern PATTERN = RstPattern.UNDERLINE.getPattern();

    @Override
    public Pattern getPattern()
    {
        throw new UnsupportedOperationException("Unimplemented method 'getPattern()'. Use 'getMatches()' instead.");
    }

    @Override
    public Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        if (line != null && nextLine != null && PATTERN.matcher(nextLine).matches())
        {
            return Optional.of(List.of(line));
        }
        return Optional.empty();
    }
}
