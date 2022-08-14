package org.itsallcode.openfasttrace.core;

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;

/**
 * Provides convenient methods for importing, tracing and reporting.
 */
public class OftRunner implements Oft
{
    private final ServiceFactory serviceFactory;

    /** Create a new instance. */
    public OftRunner()
    {
        this(new ServiceFactory());
    }

    OftRunner(ServiceFactory serviceFactory)
    {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public List<SpecificationItem> importItems(final ImportSettings settings)
    {
        return serviceFactory.createImporterService(settings) //
                .createImporter() //
                .importAny(settings.getInputs()) //
                .getImportedItems();
    }

    @Override
    public List<SpecificationItem> importItems()
    {
        return importItems(ImportSettings.createDefault());
    }

    @Override
    public List<LinkedSpecificationItem> link(final List<SpecificationItem> items)
    {
        return serviceFactory.createLinker(items).link();
    }

    @Override
    public Trace trace(final List<LinkedSpecificationItem> linkedItems)
    {
        return serviceFactory.createTracer().trace(linkedItems);
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
        serviceFactory.createExporterService().exportToPath(items.stream(), path, settings);
    }

    @Override
    public void reportToStdOut(final Trace trace)
    {
        final ReportSettings settings = ReportSettings.createDefault();
        serviceFactory.createReportService(settings).reportTraceToStdOut(trace,
                settings.getOutputFormat());
    }

    @Override
    public void reportToStdOut(final Trace trace, final ReportSettings settings)
    {
        serviceFactory.createReportService(settings).reportTraceToStdOut(trace,
                settings.getOutputFormat());
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath)
    {
        final ReportSettings settings = ReportSettings.createDefault();
        serviceFactory.createReportService(settings).reportTraceToPath(trace, outputPath,
                settings.getOutputFormat());
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath,
            final ReportSettings settings)
    {
        serviceFactory.createReportService(settings).reportTraceToPath(trace, outputPath,
                settings.getOutputFormat());
    }
}
