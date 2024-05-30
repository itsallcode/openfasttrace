package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import org.itsallcode.openfasttrace.importer.lightweightmarkup.LineParserState;

/**
 * Transition in the line parser statemachine.
 */
public class Transition
{
    private final LineParserState from;
    private final LineParserState to;
    private final LinePattern linePattern;
    private final TransitionAction transitionAction;

    /**
     * Create a new instance of a {@link Transition}.
     *
     * @param from
     *            state the statemachine comes from
     * @param to
     *            state the machine will switch to if the pattern matches
     * @param linePattern
     *            pattern the line must match for the transition to happen
     * @param transitionAction
     *            action that will be executed as result of the transition
     */
    public Transition(final LineParserState from, final LineParserState to, final LinePattern linePattern,
            final TransitionAction transitionAction)
    {
        this.from = from;
        this.to = to;
        this.linePattern = linePattern;
        this.transitionAction = transitionAction;
    }

    /**
     * Get the origin state of this transition.
     *
     * @return origin state
     */
    public LineParserState getFrom()
    {
        return this.from;
    }

    /**
     * Get the target state of this transition.
     *
     * @return target state
     */
    public LineParserState getTo()
    {
        return this.to;
    }

    /**
     * Get the regular expression pattern that needs to be matched in order for
     * the transition to happen.
     *
     * @return line pattern to be matched
     */
    public LinePattern getLinePattern()
    {
        return this.linePattern;
    }

    /**
     * Get the action that is executed when the transition happens.
     *
     * @return action that is executed as result of the transition
     */
    public TransitionAction getTransitionAction()
    {
        return this.transitionAction;
    }

    @Override
    public String toString()
    {
        return "Transition [from=" + this.from + ", to=" + this.to + ", markdownPattern="
                + this.linePattern + "]";
    }
}
