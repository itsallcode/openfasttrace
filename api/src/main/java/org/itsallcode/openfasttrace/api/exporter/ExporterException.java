package org.itsallcode.openfasttrace.api.exporter;

/**
 * {@link Exporter}s throw this exception when there is an error while
 * exporting.
 */
public class ExporterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link ExporterException}.
     * 
     * @param message
     *            the message for the exception.
     * @param cause
     *            the cause for the exception.
     */
    public ExporterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new {@link ExporterException}.
     * 
     * @param message
     *            the message for the exception.
     */
    public ExporterException(final String message)
    {
        super(message);
    }
}
