package org.itsallcode.openfasttrace.testutil.matcher;

/*-
 * #%L
 * OpenFastTrace Test utilities
 * %%
 * Copyright (C) 2016 - 2021 itsallcode.org
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
