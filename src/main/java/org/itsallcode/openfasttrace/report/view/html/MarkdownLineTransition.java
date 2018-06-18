package org.itsallcode.openfasttrace.report.view.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * The {@link MarkdownLineTransition} is a transition in the
 * {@link MarkdownLineStateMachine}.
 */
class MarkdownLineTransition
{
    private final MarkdownLineState from;
    private final MarkdownLineState to;
    private final Pattern pattern;
    private final String prefix;
    private final String postfix;
    private final Function<String, String> conversion;

    /**
     * Create a new instance of {@link MarkdownLineTransition}
     * 
     * @param from
     *            origin state of the transition
     * @param to
     *            target state of the transition
     * @param pattern
     *            pattern that must be matched to cause this transition
     * @param postfix
     *            postfix for the previous line
     * @param prefix
     *            prefix for the new line
     * @param conversion
     *            conversion to be applied on the line
     */
    public MarkdownLineTransition(final MarkdownLineState from, final MarkdownLineState to,
            final String pattern, final String prefix, final String postfix,
            final Function<String, String> conversion)
    {
        super();
        this.from = from;
        this.to = to;
        this.pattern = Pattern.compile(pattern);
        this.prefix = prefix;
        this.postfix = postfix;
        this.conversion = conversion;
    }

    /**
     * Get the origin state of the transition
     * 
     * @return the origin state
     */
    public MarkdownLineState getFrom()
    {
        return this.from;
    }

    /**
     * Get the target state of the transition
     * 
     * @return the target state
     */
    public MarkdownLineState getTo()
    {
        return this.to;
    }

    /**
     * Get the pattern that must be matched to cause this transition
     * 
     * @return the trigger pattern
     */
    public Pattern getPattern()
    {
        return this.pattern;
    }

    /**
     * Get the prefix for the new line
     * 
     * @return the prefix for the new line
     */
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * Get the postfix for the previous line
     * 
     * @return the postfix for the previous line
     */
    public String getPostfix()
    {
        return this.postfix;
    }

    /**
     * Get the conversion function to be applied on the line
     * 
     * @return the conversion function
     */
    public Function<String, String> getConversion()
    {
        return this.conversion;
    }
}
