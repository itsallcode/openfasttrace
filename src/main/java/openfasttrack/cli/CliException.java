package openfasttrack.cli;

public class CliException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public CliException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public CliException(final String message)
    {
        super(message);
    }
}
