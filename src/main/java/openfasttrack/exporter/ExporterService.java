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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import openfasttrack.core.LinkedSpecificationItem;

public class ExporterService
{
    private static final String DEFAULT_OUTPUT_FORMAT = "specobject";
    private final ExporterFactoryLoader factoryLoader;

    public ExporterService()
    {
        this(new ExporterFactoryLoader());
    }

    public ExporterService(final ExporterFactoryLoader factoryLoader)
    {
        this.factoryLoader = factoryLoader;
    }

    /**
     * Export the given {@link LinkedSpecificationItem} in the given output
     * format to a file
     *
     * @param linkedSpecItemStream
     *            the {@link LinkedSpecificationItem} to export
     * @param outputFormat
     *            the output format
     * @param outputFile
     *            the output file
     */
    public void exportFile(final Stream<LinkedSpecificationItem> linkedSpecItemStream,
            final String outputFormat, final Path outputFile)
    {
        final String outputFormatToUse = outputFormat == null ? DEFAULT_OUTPUT_FORMAT
                : outputFormat;
        final ExporterFactory factory = this.factoryLoader
                .getExporterFactory(outputFormatToUse);
        factory.createExporter(outputFile, outputFormatToUse, StandardCharsets.UTF_8,
                linkedSpecItemStream).runExport();
    }
}
