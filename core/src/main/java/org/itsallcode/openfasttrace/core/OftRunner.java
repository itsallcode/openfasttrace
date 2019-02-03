package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.ExportSettings;
import org.itsallcode.openfasttrace.Oft;
import org.itsallcode.openfasttrace.ReportSettings;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.itsallcode.openfasttrace.exporter.ExporterService;
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.report.ReportService;

public class OftRunner implements Oft
{
    @Override
    public List<SpecificationItem> importItems(final ImportSettings settings)
    {
        return createImporterService(settings) //
                .createImporter() //
                .importAny(settings.getInputs()) //
                .getImportedItems();
    }

    private ImporterService createImporterService(final ImportSettings settings)
    {
        final ImporterContext context = new ImporterContext(settings);
        final InitializingServiceLoader<ImporterFactory, ImporterContext> serviceLoader = InitializingServiceLoader
                .load(ImporterFactory.class, context);
        final ImporterService service = new ImporterServiceImpl(
                new ImporterFactoryLoader(serviceLoader), settings);
        context.setImporterService(service);
        return service;
    }

    @Override
    public List<SpecificationItem> importItems()
    {
        return importItems(ImportSettings.createDefault());
    }

    @Override
    public List<LinkedSpecificationItem> link(final List<SpecificationItem> items)
    {
        return new Linker(items).link();
    }

    @Override
    public Trace trace(final List<LinkedSpecificationItem> linkedItems)
    {
        return new Tracer().trace(linkedItems);
    }

    @Override
    public void exportToPath(final List<SpecificationItem> items, final Path path)
    {
        exportToPath(items, path, ExportSettings.createDefault());
    }

    @Override
    public void exportToPath(final List<SpecificationItem> items, final Path path,
            final ExportSettings settings)
    {
        new ExporterService().exportToPath(items.stream(), path, settings);
    }

    @Override
    public void reportToStdOut(final Trace trace)
    {
        final ReportSettings settings = ReportSettings.createDefault();
        new ReportService(settings).reportTraceToStdOut(trace, settings.getOutputFormat());
    }

    @Override
    public void reportToStdOut(final Trace trace, final ReportSettings settings)
    {
        new ReportService(settings).reportTraceToStdOut(trace, settings.getOutputFormat());
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath)
    {
        ReportSettings settings = ReportSettings.createDefault();
        new ReportService(settings).reportTraceToPath(trace, outputPath,
                settings.getOutputFormat());
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath,
            final ReportSettings settings)
    {
        new ReportService(settings).reportTraceToPath(trace, outputPath,
                settings.getOutputFormat());
    }
}
