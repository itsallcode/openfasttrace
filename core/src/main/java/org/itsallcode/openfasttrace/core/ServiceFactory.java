package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace Core
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.itsallcode.openfasttrace.exporter.ExporterFactoryLoader;
import org.itsallcode.openfasttrace.exporter.ExporterService;
import org.itsallcode.openfasttrace.importer.ImporterFactoryLoader;
import org.itsallcode.openfasttrace.importer.ImporterServiceImpl;
import org.itsallcode.openfasttrace.report.ReportService;
import org.itsallcode.openfasttrace.report.ReporterFactoryLoader;

class ServiceFactory
{
    ExporterService createExporterService()
    {
        return new ExporterService(new ExporterFactoryLoader(new ExporterContext()));
    }

    ImporterService createImporterService(final ImportSettings settings)
    {
        final ImporterContext context = new ImporterContext(settings);
        final InitializingServiceLoader<ImporterFactory, ImporterContext> serviceLoader = InitializingServiceLoader
                .load(ImporterFactory.class, context);
        final ImporterService service = new ImporterServiceImpl(
                new ImporterFactoryLoader(serviceLoader), settings);
        context.setImporterService(service);
        return service;
    }

    Linker createLinker(List<SpecificationItem> items)
    {
        return new Linker(items);
    }

    Tracer createTracer()
    {
        return new Tracer();
    }

    ReportService createReportService(final ReportSettings settings)
    {
        return new ReportService(new ReporterFactoryLoader(new ReporterContext(settings)));
    }
}
