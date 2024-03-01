package org.itsallcode.openfasttrace.api.importer;

/**
 * {@link Importer}s throw this exception when there is an error while
 * importing.
 */
public class ImporterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link ImporterException}.
     * 
     * @param message
     *            the message for the exception.
     * @param cause
     *            the cause for the exception.
     */
    public ImporterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new {@link ImporterException}.
     * 
     * @param message
     *            the message for the exception.
     */
    public ImporterException(final String message)
    {
        super(message);
    }
}
