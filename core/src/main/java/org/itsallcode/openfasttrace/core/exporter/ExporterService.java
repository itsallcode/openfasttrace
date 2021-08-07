package org.itsallcode.openfasttrace.core.exporter;

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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.core.ExportSettings;

/**
 * This provides a convenient method for exporting {@link SpecificationItem}s to
 * a file.
 */
public class ExporterService
{
    private final ExporterFactoryLoader factoryLoader;

    /**
     * Creates a new service.
     * 
     * @param factoryLoader
     *            the loader used for locating exporters.
     */
    public ExporterService(final ExporterFactoryLoader factoryLoader)
    {
        this.factoryLoader = factoryLoader;
    }

    /**
     * Export the given {@link LinkedSpecificationItem} in the given output
     * format to a file
     *
     * @param itemStream
     *            {@link SpecificationItem} to export
     * @param outputFile
     *            path to which the export is written
     * @param settings
     *            exporter settings
     */
    public void exportToPath(final Stream<SpecificationItem> itemStream, final Path outputFile,
            final ExportSettings settings)
    {
        final ExporterFactory factory = this.factoryLoader
                .getExporterFactory(settings.getOutputFormat());
        factory.createExporter(outputFile, settings.getOutputFormat(), StandardCharsets.UTF_8,
                settings.getNewline(), itemStream).runExport();
    }
}
