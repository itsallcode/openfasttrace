package org.itsallcode.openfasttrace.importer.restructuredtext;

import java.util.List;
import java.util.Optional;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LinePattern;

class RstSectionTitlePattern implements LinePattern
{
    private static final LinePattern UNDERLINE = RstPattern.UNDERLINE.getPattern();

    @Override
    public Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        if (line != null && nextLine != null && UNDERLINE.getMatches(nextLine, null).isPresent())
        {
            return Optional.of(List.of(line));
        }
        return Optional.empty();
    }
}
