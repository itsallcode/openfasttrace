package org.itsallcode.openfasttrace.testutil.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.itsallcode.matcher.auto.AutoMatcher.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class StringRegexpMatcherTest
{
    private static final String NL = System.lineSeparator();

    @Test
    void testMatches()
    {
        assertDoesNotThrow(() -> assertThat("abc", new StringRegexpMatcher("a.c")));
    }

    @Test
    void testIgnoresNewlines()
    {
        assertDoesNotThrow(() -> assertThat("ab\nc", new StringRegexpMatcher("a.c")));
    }

    @Test
    void testDoesNotMatch()
    {
        final StringRegexpMatcher matcher = new StringRegexpMatcher("a.c");
        final AssertionError error = assertThrows(AssertionError.class, () -> assertThat("ac", matcher));
        assertThat(error.getMessage(), equalTo(NL + "Expected: a string contains regexp \"a.c\"" + NL + //
                "     but: was \"ac\""));
    }

    @Test
    void testMatchesCase()
    {
        final StringRegexpMatcher matcher = new StringRegexpMatcher("abc");
        final AssertionError error = assertThrows(AssertionError.class, () -> assertThat("aBc", matcher));
        assertThat(error.getMessage(), equalTo(NL + "Expected: a string contains regexp \"abc\"" + NL + //
                "     but: was \"aBc\""));
    }
}
