package openfasttrack.matcher;

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
            if (i > originalLineCount)
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
     * @return
     */
    public static MultilineTextMatcher matchesAllLines(final String text)
    {
        return new MultilineTextMatcher(text);
    }
}