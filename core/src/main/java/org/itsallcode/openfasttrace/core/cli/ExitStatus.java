package org.itsallcode.openfasttrace.core.cli;

/**
 * Exit status of the OpenFastTrace command line interface.
 */
public enum ExitStatus
{
    /** Process finished successfully. */
    OK(0),
    /** An error occurred during processing. */
    FAILURE(1),
    /** Got invalid command line arguments. */
    CLI_ERROR(2);

    private final int code;

    ExitStatus(final int code)
    {
        this.code = code;
    }

    /**
     * Get the numeric representation of the exit status code
     * 
     * @return exit status code as integer number
     */
    public int getCode()
    {
        return this.code;
    }
}
