package openfasttrack.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.importer.ImporterException;

/**
 * Super class for factories producing {@link Exporter}s.
 */
public abstract class ExporterFactory
{
    private final static Logger LOG = Logger.getLogger(ExporterFactory.class.getName());
    private final String supportedOutputFormat;

    protected ExporterFactory(final String supportedOutputFormat)
    {
        this.supportedOutputFormat = supportedOutputFormat;
    }

    /**
     * Returns <code>true</code> if this {@link ExporterFactory} supports
     * exporting the given output format.
     *
     * @param type
     *            the output type to check.
     * @return <code>true</code> if the given type is supported for exporting.
     */
    public boolean supportsFormat(final String type)
    {
        return this.supportedOutputFormat.equals(type);
    }

    /**
     * Create an exporter that is able to export the given output format.
     *
     * @param file
     *            the file to which specification items are written
     * @param outputFormat
     *            the output format
     * @param charset
     *            the charset used for exporting
     * @param items
     *            the items to export
     * @return an exporter instance
     */
    public Exporter createExporter(final Path file, final String outputFormat,
            final Charset charset, final List<LinkedSpecificationItem> items)
    {
        if (!supportsFormat(outputFormat))
        {
            throw new ImporterException(
                    "Output format '" + outputFormat + "' not supported for export");
        }
        LOG.finest(
                () -> "Creating exporter for file " + file + " and output format " + outputFormat);
        final BufferedWriter writer = createWriter(file, charset);
        return createExporter(writer, items);
    }

    private BufferedWriter createWriter(final Path file, final Charset charset)
    {
        try
        {
            return Files.newBufferedWriter(file, charset);
        }
        catch (final IOException e)
        {
            throw new ExporterException("Error creating writer for file " + file, e);
        }
    }

    /**
     * Create an exporter that is able to write to the given file.
     *
     * @param writer
     *            the {@link Writer} to which specification items are exported
     * @param items
     *            the items to export
     * @return an {@link Exporter} instance
     */
    protected abstract Exporter createExporter(final Writer writer,
            List<LinkedSpecificationItem> items);
}