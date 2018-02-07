package org.itsallcode.openfasttrace.importer.markdown;

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

import java.util.regex.Matcher;

/**
 * This machine implements the core of a state based parser
 *
 * Before the state machine is run, it needs to be configured with a transition
 * table in the constructor.
 *
 * Each step of the state machine gets a portion of the text to be imported as
 * input. The machine checks the current state and the input on each step and
 * decides on resulting state and action depending on the configuration provided
 * in the transition table.
 */
public class MarkdownImporterStateMachine
{
    private State state = State.START;
    private String lastToken = "";
    private final Transition[] transitions;

    /**
     * Create a new instance of the {@link MarkdownImporterStateMachine}
     *
     * @param transitions
     *            the transition table that serves as configuration for the
     *            state machine
     */
    public MarkdownImporterStateMachine(final Transition[] transitions)
    {
        this.transitions = transitions;
    }

    /**
     * Step the state machine
     *
     * @param line
     *            the text fragment on which the state machine decides the next
     *            state and action
     */
    public void step(final String line)
    {
        for (final Transition entry : this.transitions)
        {
            if ((this.state == entry.getFrom()) && matchToken(line, entry))
            {
                entry.getTransition().transit();
                this.state = entry.getTo();
                break;
            }
        }
    }

    private boolean matchToken(final String line, final Transition entry)
    {
        boolean matches = false;
        final Matcher matcher = entry.getMarkdownPattern().getPattern().matcher(line);
        if (matcher.matches())
        {
            this.lastToken = (matcher.groupCount() == 0) ? "" : matcher.group(1);
            matches = true;
        }
        return matches;
    }

    /**
     * Get the last text token that the state machine isolated
     *
     * @return the last text token
     */
    public String getLastToken()
    {
        return this.lastToken;
    }
}
