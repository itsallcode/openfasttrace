package openfasttrack.exporter;

public class ExporterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public ExporterException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public ExporterException(final String message)
    {
        super(message);
    }
}
