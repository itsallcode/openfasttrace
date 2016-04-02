package openfasttrack.cli;

public class MissingArgumentException extends CliException
{
    private static final long serialVersionUID = 1L;

    public MissingArgumentException(final String argumentName)
    {
        super("Argument '" + argumentName + "' is missing");
    }
}
