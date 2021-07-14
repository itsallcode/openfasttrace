package org.itsallcode.openfasttrace.api.core;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.util.regex.Pattern;

/**
 * This enumeration represents the different types of newline.
 */
public enum Newline
{
    /** Newline character used under Unix. */
    UNIX("\n"),
    /** Newline characters used under windows. */
    WINDOWS("\r\n"),
    /** Newline character used on old macs. */
    OLDMAC("\r");

    private static final String ANY_NEWLINE_REG_EX = "\\r\\n|\\r|\\n";
    private static final Pattern ANY_NEWLINE_PATTERN = Pattern.compile(ANY_NEWLINE_REG_EX);
    private final String representation;

    private Newline(final String representation)
    {
        this.representation = representation;
    }

    @Override
    public String toString()
    {
        return this.representation;
    }

    /**
     * Get the {@link Newline} enumeration value from a string representation.
     * 
     * @param representation
     *            the string representation
     * @return the enumeration value
     */
    public static Newline fromRepresentation(final String representation)
    {
        switch (representation)
        {
        case "\n":
            return UNIX;
        case "\r\n":
            return WINDOWS;
        case "\r":
            return OLDMAC;
        default:
            throw new IllegalArgumentException(
                    "Line separator not supported: '" + representation + "'");
        }
    }

    /**
     * Get a regular expression matching any kind of newline
     * 
     * @return a regular expression matching any newline
     */
    public static String anyNewlineReqEx()
    {
        return ANY_NEWLINE_REG_EX;
    }

    /**
     * Get a pre-compiled pattern matching any kind of newline
     * 
     * @return a pattern matching any kind of newline
     */
    public static Pattern anyNewlinePattern()
    {
        return ANY_NEWLINE_PATTERN;
    }
}
