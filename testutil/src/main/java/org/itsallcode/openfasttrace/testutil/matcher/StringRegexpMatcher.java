package org.itsallcode.openfasttrace.testutil.matcher;

import org.hamcrest.core.SubstringMatcher;

import java.util.regex.Pattern;

/**
 * A Matcher that compares a string with a regular expression.
 */
public class StringRegexpMatcher extends SubstringMatcher
{
    public StringRegexpMatcher(String regexp)
    {
        super(regexp);
    }

    protected boolean evalSubstringOf(final String value)
    {
        return Pattern.matches(this.substring, value.replace("\n", ""));
    }

    protected String relationship()
    {
        return "contains regexp";
    }

}
