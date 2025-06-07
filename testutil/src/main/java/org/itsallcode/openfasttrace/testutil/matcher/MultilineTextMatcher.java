package org.itsallcode.openfasttrace.testutil.matcher;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This class implements a matcher for multiline text that helps find the
 * differences more quickly.
 */
public class MultilineTextMatcher extends TypeSafeMatcher<String>
{
    private static final String LINE_ENDING = "\\r\\n|\\r|\\n";
    private final String originalText;
    private String text;

    /**
     * Creates a new matcher for the given text.
     * 
     * @param originalText
     *            text to match against
     */
    public MultilineTextMatcher(final String originalText)
    {
        this.originalText = originalText;
    }

    /**
     * Match the text against the original text.
     * <p>
     * A full match (including line separators) is required.
     * </p>
     * @param text
     *            text to be matched against the original text.
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
        final List<String> originalLines = splitPreservingNewLines(this.originalText);
        final List<String> lines = splitPreservingNewLines(this.text);

        final int originalLineCount = originalLines.size();
        final int lineCount = lines.size();

        mismatchDescription.appendText(describeLineCount(lineCount));

        for (int i = 0; i < lineCount; ++i)
        {
            final String line = lines.get(i);
            if (i > originalLineCount - 1)
            {
                mismatchDescription.appendText("+>> ");
            }
            else if (line.equals(originalLines.get(i)))
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
        final List<String> originalLines = splitPreservingNewLines(this.originalText);
        final int originalLineCount = originalLines.size();

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
     * @param lines
     *            lines of text
     * @return multiline text matcher
     */
    public static MultilineTextMatcher matchesAllLines(final String... lines)
    {
        return new MultilineTextMatcher(String.join(System.lineSeparator(), lines));
    }

    private List<String> splitPreservingNewLines(final String text)
    {

        final String lineSplittingRegEx = "(?<=" + LINE_ENDING + ")";
        final List<String> lines = new ArrayList<>();
        for (String line : text.split(lineSplittingRegEx))
        {
            line = line.replace('\n', '\u240A').replace('\r', '\u240D').replace('\t', '\u2409');
            lines.add(line);
        }
        return lines;
    }
}
