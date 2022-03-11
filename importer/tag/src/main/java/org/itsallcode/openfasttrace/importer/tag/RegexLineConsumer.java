package org.itsallcode.openfasttrace.importer.tag;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
