package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
    private static final Pattern PARSER_OFF_PATTERN = Pattern.compile("(?:^|\\W)oft:off(?:\\W||$)");
    private static final Pattern PARSER_ON_PATTERN = Pattern.compile("(?:^|\\W)oft:on(?:\\W|$)");

    private LineParserState state = LineParserState.START;
    private String lastToken = "";
    private final Transition[] transitions;
    private boolean enabled = true;

    /**
     * Create a new instance of the {@link LineParserStateMachine}
     *
     * @param transitions
     *            the transition table that serves as configuration for the
     *            state machine
     */
    public LineParserStateMachine(final Transition[] transitions)
    {
        this.transitions = Arrays.copyOf(transitions, transitions.length);
    }

    /**
     * Step the state machine.
     *
     * @param line
     *            the text fragment on which the state machine decides the next
     *            state and action
     * @param nextLine
     *            the following line or {@code null} if the current line is the
     *            last one in the file. This is useful as a lookahead for
     *            patterns that span multiple lines like underlined titles in
     *            Markdown or RST.
     */
    // [impl -> dsn~disabling-oft-parsing-for-parts-of-a-markdown-file~1]
    // [impl -> dsn~disabling-oft-parsing-for-parts-of-an-rst-file~1]
    public void step(final String line, final String nextLine)
    {
        if (enabled) {
            if (PARSER_OFF_PATTERN.matcher(line).find()) {
                enabled = false;
            } else {
                stepEnabled(line, nextLine);
            }
        } else if (PARSER_ON_PATTERN.matcher(line).find()) {
            enabled = true;
        }
    }

    private void stepEnabled(final String line, final String nextLine) {
        boolean matched = false;
        for (final Transition entry : this.transitions) {
            if ((this.state == entry.getFrom()) && matchToken(line, nextLine, entry)) {
                LOG.finest(() -> entry + " : '" + line + "'");
                entry.getTransitionAction().transit();
                this.state = entry.getTo();
                matched = true;
                break;
            }
        }
        if (!matched) {
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

    /**
     * Get the current state of the state machine.
     * <p>
     * This method is package private because it used only for testing.
     * </p>
     * 
     * @return the current state of the state machine
     */
    LineParserState getState()
    {
        return this.state;
    }
}
