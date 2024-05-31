package org.itsallcode.openfasttrace.importer.markdown;

import java.util.List;
import java.util.Optional;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.LinePattern;

class MdSectionTitlePattern implements LinePattern
{
    private static final LinePattern UNDERLINE = MdPattern.UNDERLINE.getPattern();
    private static final LinePattern HASH_TITLE = MdPattern.TITLE.getPattern();

    @Override
    public Optional<List<String>> getMatches(final String line, final String nextLine)
    {
        if (line == null)
        {
            return Optional.empty();
        }
        final Optional<List<String>> hashTitle = HASH_TITLE.getMatches(line, null);
        if (hashTitle.isPresent())
        {
            return hashTitle;
        }
        if (nextLine != null && UNDERLINE.getMatches(nextLine, null).isPresent())
        {
            return Optional.of(List.of(line));
        }
        return Optional.empty();
    }
}
