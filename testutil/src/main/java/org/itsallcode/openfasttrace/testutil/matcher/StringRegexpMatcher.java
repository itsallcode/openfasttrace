package org.itsallcode.openfasttrace.testutil.matcher;

import java.util.regex.Pattern;

import org.hamcrest.core.SubstringMatcher;

/**
 * A Matcher that compares a string with a regular expression.
 */
public class StringRegexpMatcher extends SubstringMatcher
{
    public StringRegexpMatcher(final String regexp)
    {
        super("contains regexp", false, regexp);
    }

    protected boolean evalSubstringOf(final String value)
    {
        return Pattern.matches(this.substring, value.replace("\n", ""));
    }
}
