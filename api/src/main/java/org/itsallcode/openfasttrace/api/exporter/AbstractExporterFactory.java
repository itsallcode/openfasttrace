package org.itsallcode.openfasttrace.api.exporter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;

/**
 * Base class for {@link ExporterFactory} implementations that share exporter logic.
 */
public abstract class AbstractExporterFactory implements ExporterFactory
{
    private static final Logger LOG = Logger.getLogger(AbstractExporterFactory.class.getName());

    private final String supportedOutputFormat;

    private ExporterContext context;

    /**
     * Create a new {@link AbstractExporterFactory}.
     * 
     * @param supportedOutputFormat
     *            the format of the supported output format, e.g.
     *            {@code "html"}.
     */
    protected AbstractExporterFactory(final String supportedOutputFormat)
    {
        this.supportedOutputFormat = supportedOutputFormat;
    }

    @Override
    public void init(final ExporterContext context)
    {
        this.context = context;
    }

    /**
     * Get the {@link ExporterContext} set by the {@link #init(ExporterContext)}
     * method.
     * 
     * @return the {@link ExporterContext}.
     */
    @Override
    public ExporterContext getContext()
    {
        return this.context;
    }

    @Override
    public boolean supportsFormat(final String format)
    {
        return this.supportedOutputFormat.equals(format);
    }

    @Override
    public Exporter createExporter(final Path file, final String format, final Charset charset,
            final Newline newline, final Stream<SpecificationItem> itemStream)
    {
        if (!supportsFormat(format))
        {
            throw new ExporterException("Output format '" + format + "' not supported for export");
        }
        final Writer writer = createWriter(file, charset);
        return createExporter(writer, itemStream, newline);
    }

    private static Writer createWriter(final Path file, final Charset charset)
    {
        if (file == null)
        {
            LOG.finest(() -> "Creating exporter for STDOUT using charset " + charset);
            return new OutputStreamWriter(getStdOutStream(), charset);
        }
        LOG.finest(() -> "Creating exporter for file " + file + " using charset " + charset);
        return createFileWriter(file, charset);
    }

    // Using System.out by intention
    @SuppressWarnings("squid:S106")
    private static PrintStream getStdOutStream()
    {
        return System.out;
    }

    private static Writer createFileWriter(final Path file, final Charset charset)
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
}
