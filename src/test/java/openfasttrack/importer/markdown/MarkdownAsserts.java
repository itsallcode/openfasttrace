package openfasttrack.importer.markdown;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

public class MarkdownAsserts
{
    public static void assertMatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, true);
    }

    public static void assertMismatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, false);
    }

    public static void assertMatching(final String[] samples, final MdPattern mdPattern,
            final boolean mustMatch)
    {
        for (final String text : samples)
        {
            final Matcher matcher = mdPattern.getPattern().matcher(text);
            assertThat(mdPattern.toString() + " must " + (mustMatch ? "" : "not ") + "match " + "\""
                    + text + "\"", matcher.matches(), equalTo(mustMatch));
        }
    }
}