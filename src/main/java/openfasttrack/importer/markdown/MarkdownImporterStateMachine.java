package openfasttrack.importer.markdown;

import java.util.regex.Matcher;

import openfasttrack.importer.ImportEventListener;

public class MarkdownImporterStateMachine
{
    private final ImportEventListener listener;
    private State state = State.START;
    private String lastToken = "";

    public MarkdownImporterStateMachine(final ImportEventListener listener)
    {
        this.listener = listener;
    }

    public void step(final String line)
    {
        for (final Transition entry : this.transitions)
        {
            if (this.state == entry.getFrom())
            {
                matchToken(line, entry);
            }
        }
    }

    private void matchToken(final String line, final Transition entry)
    {
        final Matcher matcher = entry.getMarkdownPattern().getPattern().matcher(line);
        if (matcher.matches())
        {
            if (matcher.groupCount() > 0)
            {
                this.lastToken = matcher.group(1);
            }
            executeTransition(line, entry);
        }
    }

    private void executeTransition(final String line, final Transition entry)
    {
        entry.getTransition().transit();
        System.out.println(this.state + " -> " + entry.getTo() + " : " + line);
        this.state = entry.getTo();
    }

    private void emitNewId()
    {
        this.listener.foundNewSpecificationItem(this.lastToken);
    }

    private void emitAddCoverage()
    {
        this.listener.addCoverage(this.lastToken);
    }

    // @formatter:off
    private final Transition[] transitions = {
            new Transition(State.START    , State.OUTSIDE  , MdPattern.EVERYTHING, () -> {}),
            new Transition(State.START    , State.SPEC_ITEM, MdPattern.ID        , () -> this.emitNewId()),
            new Transition(State.OUTSIDE  , State.SPEC_ITEM, MdPattern.ID        , () -> this.emitNewId()),
            new Transition(State.SPEC_ITEM, State.COVERS   , MdPattern.COVERS    , () -> {}),
            new Transition(State.COVERS   , State.COVERS   , MdPattern.REFERENCE , () -> this.emitAddCoverage())
    };
    // @formatter:on
}