package openfasttrack.matcher;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This class implements a matcher for multiline text that helps finding the
 * differences more quickly.
 */
public class MultilineTextMatcher extends TypeSafeMatcher<String>
{
    private static final String LINE_ENDING = "\\r\\n|\\r|\\n";
    private final String originalText;
    private String text;

    public MultilineTextMatcher(final String originalText)
    {
        this.originalText = originalText;
    }

    /**
     * Match the text against the original text.
     * 
     * A full match (including line separators) is required.
     * 
     * @param text
     *            the text to be matched against the original text.
     */
    @Override
    public boolean matchesSafely(final String text)
    {
        this.text = text;
        return text.equals(this.originalText);
    }

    @Override
    protected void describeMismatchSafely(final String text, final Description mismatchDescription)
    {
        final String[] originalLines = this.originalText.split(LINE_ENDING);
        final String[] lines = this.text.split(LINE_ENDING);

        final int originalLineCount = originalLines.length;
        final int lineCount = lines.length;

        mismatchDescription.appendText(describeLineCount(lineCount));

        for (int i = 0; i < lineCount; ++i)
        {
            final String line = lines[i];
            if (i > originalLineCount - 1)
            {
                mismatchDescription.appendText("+>> ");
            }
            else if (line.equals(originalLines[i]))
            {
                mismatchDescription.appendText("    ");
            }
            else
            {
                mismatchDescription.appendText(">>> ");
            }
            mismatchDescription.appendText(line);
            mismatchDescription.appendText(System.lineSeparator());
        }
    }

    private String describeLineCount(final int lineCount)
    {
        return "(" + lineCount + " lines)" + System.lineSeparator();
    }

    @Override
    public void describeTo(final Description description)
    {
        final String[] originalLines = this.originalText.split(LINE_ENDING);
        final int originalLineCount = originalLines.length;

        description.appendText(describeLineCount(originalLineCount));
        for (final String line : originalLines)
        {
            description.appendText("    ");
            description.appendText(line);
            description.appendText(System.lineSeparator());
        }
    }

    /**
     * Factory method for multiline text matcher
     * 
     * @param text
     *            the text to be matched against the original
     * @return the matcher
     */
    public static MultilineTextMatcher matchesAllLines(final String text)
    {
        return new MultilineTextMatcher(text);
    }
}