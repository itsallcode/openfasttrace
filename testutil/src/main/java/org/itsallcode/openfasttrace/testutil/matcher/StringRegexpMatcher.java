package org.itsallcode.openfasttrace.testutil.matcher;

import java.util.regex.Pattern;

import org.hamcrest.core.SubstringMatcher;

/**
 * A Matcher that compares a string with a regular expression.
 */
public class StringRegexpMatcher extends SubstringMatcher
{
    /**
     * Create a new instance of the {@link StringRegexpMatcher}.
     *
     * @param regexp regular expression to match against
     */
    public StringRegexpMatcher(final String regexp)
    {
        super("contains regexp", false, regexp);
    }

    /**
     * Check if the substring matches the given value with newlines removed.
     *
     * @param value value to match the substring against
     *
     * @return {@code true} if the given value matches the substring
     */
    protected boolean evalSubstringOf(final String value)
    {
        return Pattern.matches(this.substring, value.replace("\n", ""));
    }
}
