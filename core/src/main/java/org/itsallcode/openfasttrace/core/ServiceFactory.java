package org.itsallcode.openfasttrace.core;

import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.exporter.ExporterContext;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.core.exporter.ExporterFactoryLoader;
import org.itsallcode.openfasttrace.core.exporter.ExporterService;
import org.itsallcode.openfasttrace.core.importer.ImporterFactoryLoader;
import org.itsallcode.openfasttrace.core.importer.ImporterServiceImpl;
import org.itsallcode.openfasttrace.core.report.ReportService;
import org.itsallcode.openfasttrace.core.report.ReporterFactoryLoader;

class ServiceFactory
{
    ExporterService createExporterService()
    {
        return new ExporterService(new ExporterFactoryLoader(new ExporterContext()));
    }

    ImporterService createImporterService(final ImportSettings settings)
    {
        final ImporterContext context = new ImporterContext(settings);
        final ImporterService service = new ImporterServiceImpl(new ImporterFactoryLoader(context), settings);
        context.setImporterService(service);
        return service;
    }

    Linker createLinker(final List<SpecificationItem> items)
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
