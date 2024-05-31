package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * This machine implements the core of a state based parser.
 * <p>
 * Before the state machine is run, it needs to be configured with a transition
 * table in the constructor.
 * </p>
 * <p>
 * Each step of the state machine gets a portion of the text to be imported as
 * input. The machine checks the current state and the input on each step and
 * decides on resulting state and action depending on the configuration provided
 * in the transition table.
 * </p>
 */
public class LineParserStateMachine
{
    private static final Logger LOG = Logger.getLogger(LineParserStateMachine.class.getName());

    private LineParserState state = LineParserState.START;
    private String lastToken = "";
    private final Transition[] transitions;

    /**
     * Create a new instance of the {@link LineParserStateMachine}
     *
     * @param transitions
     *            the transition table that serves as configuration for the
     *            state machine
     */
    public LineParserStateMachine(final Transition[] transitions)
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
    public void step(final String line, final String nextLine)
    {
        boolean matched = false;
        for (final Transition entry : this.transitions)
        {
            if ((this.state == entry.getFrom()) && matchToken(line, nextLine, entry))
            {
                LOG.finest(() -> entry + " : '" + line + "'");
                entry.getTransitionAction().transit();
                this.state = entry.getTo();
                matched = true;
                break;
            }
        }
        if (!matched)
        {
            LOG.finest(() -> "Current state: " + this.state + ", no match for '" + line + "'");
        }
    }

    private boolean matchToken(final String line, final String nextLine, final Transition entry)
    {
        final Optional<List<String>> matches = entry.getLinePattern().getMatches(line, nextLine);
        if (matches.isPresent())
        {
            final List<String> groups = matches.get();
            this.lastToken = groups.isEmpty() ? "" : groups.get(0);
            return true;
        }
        else
        {
            this.lastToken = "";
            return false;
        }
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

    LineParserState getState()
    {
        return this.state;
    }
}
