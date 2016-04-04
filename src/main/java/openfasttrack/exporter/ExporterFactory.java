package openfasttrack.exporter;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        final Writer writer = createWriter(file, outputFormat, charset);
        return createExporter(writer, items);
    }

    private Writer createWriter(final Path file, final String outputFormat, final Charset charset)
    {
        if (file != null)
        {
            LOG.finest(() -> "Creating exporter for file " + file + " and output format "
                    + outputFormat);
            return createWriter(file, charset);
        }
        else
        {
            LOG.finest(() -> "Creating exporter for stdout and output format " + outputFormat);
            return new OutputStreamWriter(System.out);
        }
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