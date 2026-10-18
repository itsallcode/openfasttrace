package org.itsallcode.openfasttrace.importer.tag.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

abstract class AbstractRegexLineConsumer implements LineConsumer {
    private final Pattern pattern;

    AbstractRegexLineConsumer(final String patternRegex) {
        this(Pattern.compile(patternRegex));
    }

    private AbstractRegexLineConsumer(final Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void readLine(final int lineNumber, final String line) {
        processMatches(lineNumber, line, 0);
    }

    /**
     * Process all matches in a line, starting at the given column.
     *
     * @param lineNumber
     *                    line number, starting at {@code 1}
     * @param line
     *                    line content
     * @param startColumn
     *                    column where the search starts
     * @return number of processed matches
     */
    int processMatches(final int lineNumber, final String line, final int startColumn) {
        final Matcher matcher = this.pattern.matcher(line);
        matcher.region(startColumn, line.length());
        int counter = 0;
        while (matcher.find()) {
            processMatch(matcher, lineNumber, counter);
            counter++;
        }
        return counter;
    }

    abstract void processMatch(Matcher matcher, int lineNumber, int lineMatchCount);
}
