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
        final Matcher matcher = this.pattern.matcher(line);
        int counter = 0;
        while (matcher.find()) {
            processMatch(matcher, lineNumber, counter);
            counter++;
        }
    }

    abstract void processMatch(Matcher matcher, int lineNumber, int lineMatchCount);
}
