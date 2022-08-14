package org.itsallcode.openfasttrace.report.html.view;

import java.io.PrintStream;

/**
 * Factory that creates OFT view (e.g. for reports) and provides an output
 * stream.
 */
public abstract class AbstractViewFactory implements ViewFactory
{
    /** The output stream. */
    protected final PrintStream outputStream;

    /**
     * Create a new instance.
     * 
     * @param stream
     *            the output stream.
     */
    protected AbstractViewFactory(final PrintStream stream)
    {
        this.outputStream = stream;
    }
}
