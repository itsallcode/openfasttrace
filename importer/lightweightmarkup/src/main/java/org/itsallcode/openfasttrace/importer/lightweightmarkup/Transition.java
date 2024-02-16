package org.itsallcode.openfasttrace.importer.lightweightmarkup;

public class Transition
{
    private final LineParserState from;
    private final LineParserState to;
    private final LinePattern markdownPattern;
    private final TransitionAction transitionAction;

    public Transition(final LineParserState from, final LineParserState to, final LinePattern markdownPattern,
                      final TransitionAction transitionAction)
    {
        this.from = from;
        this.to = to;
        this.markdownPattern = markdownPattern;
        this.transitionAction = transitionAction;
    }

    public LineParserState getFrom()
    {
        return this.from;
    }

    public LineParserState getTo()
    {
        return this.to;
    }

    public LinePattern getMarkdownPattern()
    {
        return this.markdownPattern;
    }

    public TransitionAction getTransition()
    {
        return this.transitionAction;
    }

    @Override
    public String toString()
    {
        return "Transition [from=" + this.from + ", to=" + this.to + ", markdownPattern="
                + this.markdownPattern + "]";
    }
}
