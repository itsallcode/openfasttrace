package org.itsallcode.openfasttrace.api.report;

/**
 * Reporters throw this exception when there is an error while generating the
 * report.
 */
public class ReportException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link ReportException}.
     * 
     * @param message
     *            the message for the exception.
     * @param cause
     *            the cause for the exception.
     */
    public ReportException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new {@link ReportException}.
     * 
     * @param message
     *            the message for the exception.
     */
    public ReportException(final String message)
    {
        super(message);
    }
}
