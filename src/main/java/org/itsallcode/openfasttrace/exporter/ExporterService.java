package org.itsallcode.openfasttrace.exporter;

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

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.SpecificationItem;

public class ExporterService
{

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
     * Export the given {@link LinkedSpecificationItem} in the given output format
     * to a file
     *
     * @param itemStream
     *            the {@link SpecificationItem} to export
     * @param format
     *            the output format
     * @param outputFile
     *            the output file
     * @param newline
     *            the newline format
     */
    public void exportFile(final Stream<SpecificationItem> itemStream, final String format,
            final Path outputFile, final Newline newline)
    {
        final ExporterFactory factory = this.factoryLoader.getExporterFactory(format);
        factory.createExporter(outputFile, format, StandardCharsets.UTF_8, newline, itemStream)
                .runExport();
    }
}
