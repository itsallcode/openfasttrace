package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader.*;
import org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine.*;

/**
 * Base class for importers of lightweight markup text.
 */
public abstract class LightWeightMarkupImporter implements Importer, LineReaderCallback
{
    /** File to be imported */
    protected final InputFile file;
    /** Listener for import events */
    protected final ImportEventListener listener;
    /** State machine for a line-by-line parser */
    protected final LineParserStateMachine stateMachine;
    private String lastTitle;
    private boolean inSpecificationItem;
    private LineContext currentContext;

    /**
     * Create a new {@link LightWeightMarkupImporter}.
     * 
     * @param file
     *            input file
     * @param listener
     *            import event listener
     */
    // Possible 'this' escape before subclass is fully initialized:
    // LineParserStateMachine constructor does not use 'this'.
    @SuppressWarnings("this-escape")
    protected LightWeightMarkupImporter(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.listener = listener;
        this.stateMachine = new LineParserStateMachine(configureTransitions());
    }

    @Override
    public void runImport()
    {
        new LineReader(file, this).readFile();
    }

    /**
     * Define the transitions of the parser statemachine.
     * 
     * @return parser statemachine transitions
     */
    protected abstract Transition[] configureTransitions();

    @Override
    public void nextLine(final LineContext context)
    {
        this.currentContext = context;
        this.stateMachine.step(this.currentContext.currentLine(), this.currentContext.nextLine());
    }

    /**
     * Define a transition in the parser statemachine.
     * 
     * @param from
     *            state to be matched against the parsers current state
     * @param to
     *            state the parser will be in if the transition happened
     * @param pattern
     *            line pattern to be matched for this transition to happen
     * @param action
     *            action to take as during the transition
     * @return transition definition
     */
    protected static Transition transition(final LineParserState from, final LineParserState to,
            final LinePattern pattern, final TransitionAction action)
    {
        return new Transition(from, to, pattern, action);
    }

    @Override
    public void finishReading()
    {
        if (this.inSpecificationItem)
        {
            this.listener.endSpecificationItem();
        }
    }

    /**
     * Start a new specification item.
     */
    protected void beginItem()
    {
        cleanUpLastItem();
        this.inSpecificationItem = true;
        informListenerAboutNewItem();
    }

    /**
     * Force the end of an open specification item.
     */
    protected void cleanUpLastItem()
    {
        if (this.inSpecificationItem)
        {
            endItem();
        }
    }

    /**
     * Informs the listener about a new specification item, including the ID and
     * file and line where it was detected.
     */
    protected void informListenerAboutNewItem()
    {
        final String idText = this.stateMachine.getLastToken();
        final SpecificationItemId id = new SpecificationItemId.Builder(idText).build();
        this.listener.beginSpecificationItem();
        this.listener.setId(id);
        this.listener.setLocation(this.file.getPath(), this.currentContext.lineNumber());
        if (this.lastTitle != null)
        {
            this.listener.setTitle(this.lastTitle);
        }
    }

    /**
     * End a specification item gracefully.
     * <p>
     * As opposed to forcing an end at clean-up (see
     * {@link LightWeightMarkupImporter#cleanUpLastItem()}.
     * </p>
     */
    protected void endItem()
    {
        this.inSpecificationItem = false;
        resetTitle();
        this.listener.endSpecificationItem();
    }

    /**
     * Set the specification item status.
     */
    protected void setStatus()
    {
        this.listener.setStatus(ItemStatus.parseString(this.stateMachine.getLastToken()));
    }

    /**
     * Begin the textual description of the specification item.
     */
    protected void beginDescription()
    {
        this.listener.appendDescription(this.stateMachine.getLastToken());
    }

    /**
     * Append text to an existing piece of the specification item description.
     */
    protected void appendDescription()
    {
        this.listener.appendDescription(System.lineSeparator());
        this.listener.appendDescription(this.stateMachine.getLastToken());
    }

    /**
     * Begin the rationale.
     */
    protected void beginRationale()
    {
        this.listener.appendRationale(System.lineSeparator());
    }

    /**
     * Append text to an existing piece of the rationale.
     */
    protected void appendRationale()
    {
        this.listener.appendRationale(System.lineSeparator());
        this.listener.appendRationale(this.stateMachine.getLastToken());
    }

    /**
     * Begin a comment.
     */
    protected void beginComment()
    {
        this.listener.appendComment(this.stateMachine.getLastToken());
    }

    /**
     * Append text to an existing piece of the comment.
     */
    protected void appendComment()
    {
        this.listener.appendComment(System.lineSeparator());
        this.listener.appendComment(this.stateMachine.getLastToken());
    }

    /**
     * Add a dependency on another specification item by ID.
     */
    protected void addDependency()
    {
        final SpecificationItemId.Builder builder = new SpecificationItemId.Builder(
                this.stateMachine.getLastToken());
        this.listener.addDependsOnId(builder.build());
    }

    /**
     * Add artifact types that this specification item needs to be covered in.
     */
    protected void addNeeds()
    {
        final String artifactTypes = this.stateMachine.getLastToken();
        for (final String artifactType : artifactTypes.split(","))
        {
            this.listener.addNeededArtifactType(artifactType.trim());
        }
    }

    /**
     * Remember the last section title in case this turns out to be a
     * specification item.
     */
    // [impl->dsn~md.specification-item-title~1]
    protected void rememberTitle()
    {
        this.lastTitle = this.stateMachine.getLastToken();
    }

    /**
     * Reset the stored section title.
     */
    protected void resetTitle()
    {
        this.lastTitle = null;
    }

    /**
     * Add an ID for a specification item this one covers.
     */
    protected void addCoverage()
    {
        this.listener.addCoveredId(SpecificationItemId.parseId(this.stateMachine.getLastToken()));
    }

    /**
     * Add one or more tags.
     */
    protected void addTag()
    {
        final String tags = this.stateMachine.getLastToken();
        for (final String tag : tags.split(","))
        {
            this.listener.addTag(tag.trim());
        }
    }

    /**
     * Create a specification item from a forward marker.
     */
    // [impl->dsn~md.artifact-forwarding-notation~1]
    protected void forward()
    {
        final ForwardingSpecificationItem forward = new ForwardingSpecificationItem(
                this.stateMachine.getLastToken());
        this.listener.beginSpecificationItem();
        this.listener.setId(forward.getSkippedId());
        this.listener.addCoveredId(forward.getOriginalId());
        for (final String targetArtifactType : forward.getTargetArtifactTypes())
        {
            this.listener.addNeededArtifactType(targetArtifactType.trim());
        }
        this.listener.setForwards(true);
        this.listener.setLocation(this.file.getPath(), this.currentContext.lineNumber());
        this.listener.endSpecificationItem();
    }
}
