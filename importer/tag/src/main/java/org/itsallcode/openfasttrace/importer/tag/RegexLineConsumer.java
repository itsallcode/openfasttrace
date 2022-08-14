package org.itsallcode.openfasttrace.importer.tag;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.importer.tag.LineReader.LineConsumer;

abstract class RegexLineConsumer implements LineConsumer
{
    private final Pattern pattern;

    RegexLineConsumer(final String patternRegex)
    {
        this(Pattern.compile(patternRegex));
    }

    private RegexLineConsumer(final Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public void readLine(final int lineNumber, final String line)
    {
        final Matcher matcher = this.pattern.matcher(line);
        int counter = 0;
        while (matcher.find())
        {
            this.processMatch(matcher, lineNumber, counter);
            counter++;
        }
    }

    /**
     * Process a match from an input line.
     * 
     * @param matcher
     *            regular expression {@link Matcher}.
     * @param lineNumber
     *            line number of the matched the input, starting with  {@code 1} for the first line.
     * @param lineMatchCount
     *            number of the current match in the context of the current line,
     *            starting with {@code 0} for the first match in a line.
     */
    abstract void processMatch(Matcher matcher, int lineNumber, int lineMatchCount);
}
