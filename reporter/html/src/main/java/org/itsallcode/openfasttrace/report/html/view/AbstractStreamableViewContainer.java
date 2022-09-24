package org.itsallcode.openfasttrace.report.html.view;

import java.io.PrintStream;

/**
 * Abstract base class for View Containers that can be rendered to an output
 * stream.
 */
public abstract class AbstractStreamableViewContainer extends AbstractViewContainer
{
    /** The output stream. */
    protected final PrintStream stream;

    /**
     * Create a new instance.
     * 
     * @param stream
     *            the output stream.
     */
    protected AbstractStreamableViewContainer(final PrintStream stream)
    {
        this.stream = stream;
    }

    /**
     * Create a new instance.
     * 
     * @param stream
     *            the output stream.
     * @param id
     *            the id of the container.
     * @param title
     *            the title of the container.
     */
    protected AbstractStreamableViewContainer(final PrintStream stream, final String id,
            final String title)
    {
        super(id, title);
        this.stream = stream;
    }

    /**
     * Render the container.
     * 
     * @param level
     *            the indentation level.
     */
    public void renderIndentation(final int level)
    {
        this.stream.print(IndentationHelper.createIndentationPrefix(level));
    }
}
