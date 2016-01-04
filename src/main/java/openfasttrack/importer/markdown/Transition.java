package openfasttrack.importer.markdown;

class Transition
{
    private final State from;
    private final State to;
    private final MdPattern markdownPattern;
    private final TransitionAction transition;

    public Transition(final State from, final State to, final MdPattern markdownPattern,
            final TransitionAction transition)
    {
        super();
        this.from = from;
        this.to = to;
        this.markdownPattern = markdownPattern;
        this.transition = transition;
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
        return this.transition;
    }
}