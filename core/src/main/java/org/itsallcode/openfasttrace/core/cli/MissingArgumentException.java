package org.itsallcode.openfasttrace.core.cli;

/**
 * This is thrown when expected command line arguments are missing.
 */
public class MissingArgumentException extends CliException
{
    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception.
     * 
     * @param argumentName
     *            the name of the missing command line argument.
     */
    public MissingArgumentException(final String argumentName)
    {
        super("Argument '" + argumentName + "' is missing");
    }
}
