package org.itsallcode.openfasttrace.testutil.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matcher that tests a given pattern string against a string value.
 * <p>
 * The purpose is to test regular expression patterns against known-to-match
 * strings. If there is no match, the pattern contains an error. Deriving from
 * {@link TypeSafeDiagnosingMatcher} allows providing a better readable error
 * message. This is important since the logic is inverted to what a user would
 * normally expect.
 * </p>
 */
public final class PatternMatcher extends TypeSafeDiagnosingMatcher<String> {
    private final String expected;

    private PatternMatcher(final String expected)
    {
        super();
        this.expected = expected;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("matches string: " + this.expected);
    }

    @Override
    protected boolean matchesSafely(final String item, final Description mismatchDescription) {
        mismatchDescription.appendText("pattern '" + item + "' did not match string");
        return this.expected.matches(item);
    }

    /**
     * Create a new matcher that tests a given pattern against a string.
     * @param expected string that the pattern should match
     * @return matcher that can be used to test a given pattern against a string
     */
    public static PatternMatcher patternMatches(final String expected)
    {
        return new PatternMatcher(expected);
    }
}
