package openfasttrack.importer;

public class ImporterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ImporterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ImporterException(final String message)
    {
        super(message);
    }
}
