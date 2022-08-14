package org.itsallcode.openfasttrace.importer.markdown;

class Transition
{
    private final State from;
    private final State to;
    private final MdPattern markdownPattern;
    private final TransitionAction transitionAction;

    public Transition(final State from, final State to, final MdPattern markdownPattern,
            final TransitionAction transitionAction)
    {
        this.from = from;
        this.to = to;
        this.markdownPattern = markdownPattern;
        this.transitionAction = transitionAction;
    }

    public State getFrom()
    {
        return this.from;
    }

    public State getTo()
    {
        return this.to;
    }

    public MdPattern getMarkdownPattern()
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
