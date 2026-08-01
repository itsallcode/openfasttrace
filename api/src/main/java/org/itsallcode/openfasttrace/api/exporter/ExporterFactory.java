package org.itsallcode.openfasttrace.api.exporter;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;

/**
 * Interface for factories producing {@link Exporter}s.
 */
public interface ExporterFactory extends Initializable<ExporterContext>
{
    /**
     * Get the {@link ExporterContext}.
     * 
     * @return the {@link ExporterContext}.
     */
    ExporterContext getContext();

    /**
     * Returns {@code true} if this {@link ExporterFactory} supports
     * exporting the given output format.
     *
     * @param format
     *            the output type to check.
     * @return {@code true} if the given type is supported for exporting.
     */
    boolean supportsFormat(final String format);

    /**
     * Create an exporter that is able to export the given output format.
     *
     * @param file
     *            the file to which specification items are written
     * @param format
     *            the output format
     * @param charset
     *            the character set used for exporting
     * @param newline
     *            the newline format
     * @param itemStream
     *            the items to export
     * @return an exporter instance
     */
    Exporter createExporter(final Path file, final String format, final Charset charset,
            final Newline newline, final Stream<SpecificationItem> itemStream);

    /**
     * Create an exporter that is able to write to the given file.
     *
     * @param writer
     *            {@link Writer} to which specification items are exported
     * @param linkedSpecItemStream
     *            {@link Stream} of items to export
     * @param newline
     *            newline format
     * @return an {@link Exporter} instance
     */
    Exporter createExporter(final Writer writer, Stream<SpecificationItem> linkedSpecItemStream,
            final Newline newline);
}
