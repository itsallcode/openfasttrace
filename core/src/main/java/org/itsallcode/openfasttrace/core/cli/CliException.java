package org.itsallcode.openfasttrace.core.cli;

/**
 * Exception thrown in case of command line validation errors.
 */
public class CliException extends Exception
{
    private static final long serialVersionUID = 3126173961917546825L;

    /**
     * Create a new {@link CliException} caused by another exception
     * 
     * @param message
     *            error message
     * @param cause
     *            causing exception
     */
    public CliException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Create a new {@link CliException}
     * 
     * @param message
     *            error message
     */
    public CliException(final String message)
    {
        super(message);
    }
}
