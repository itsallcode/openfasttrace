package org.itsallcode.openfasttrace.api.exporter;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;

/**
 * Super class for factories producing {@link Exporter}s.
 */
public abstract class ExporterFactory implements Initializable<ExporterContext>
{
    private static final Logger LOG = Logger.getLogger(ExporterFactory.class.getName());

    private final String supportedOutputFormat;

    private ExporterContext context;

    /**
     * Creates a new {@link ExporterFactory}.
     * 
     * @param supportedOutputFormat
     *            the format of the supported output format, e.g.
     *            {@code "html"}.
     */
    protected ExporterFactory(final String supportedOutputFormat)
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
    public ExporterContext getContext()
    {
        return this.context;
    }

    /**
     * Returns <code>true</code> if this {@link ExporterFactory} supports
     * exporting the given output format.
     *
     * @param format
     *            the output type to check.
     * @return <code>true</code> if the given type is supported for exporting.
     */
    public boolean supportsFormat(final String format)
    {
        return this.supportedOutputFormat.equals(format);
    }

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

    private Writer createWriter(final Path file, final Charset charset)
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
    private PrintStream getStdOutStream()
    {
        return System.out;
    }

    private Writer createFileWriter(final Path file, final Charset charset)
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
     *            {@link Writer} to which specification items are exported
     * @param linkedSpecItemStream
     *            {@link Stream} of items to export
     * @param newline
     *            newline format
     * @return an {@link Exporter} instance
     */
    protected abstract Exporter createExporter(final Writer writer,
            Stream<SpecificationItem> linkedSpecItemStream, final Newline newline);
}