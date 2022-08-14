package org.itsallcode.openfasttrace.importer.markdown;

import java.util.logging.Logger;

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
    private static final Logger LOG = Logger
            .getLogger(MarkdownImporterStateMachine.class.getName());

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
                LOG.finest(() -> entry + " : '" + line + "'");
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
