package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import java.util.regex.Matcher;

class MarkdownAsserts
{
    static void assertMatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, true);
    }

    static void assertMismatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, false);
    }

    static void assertMatching(final String[] samples, final MdPattern mdPattern,
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
