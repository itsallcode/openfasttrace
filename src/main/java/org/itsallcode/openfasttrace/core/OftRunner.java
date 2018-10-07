package org.itsallcode.openfasttrace.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.*;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.report.ReportService;

public class OftRunner implements Oft
{
    private final List<Path> inputs = new ArrayList<>();

    @Override
    public Oft addInputs(final Path... inputs)
    {
        for (final Path input : inputs)
        {
            this.inputs.add(input);
        }
        return this;
    }

    @Override
    public Oft addInputs(final List<Path> inputs)
    {
        this.inputs.addAll(inputs);
        return this;
    }

    @Override
    public List<SpecificationItem> importItems()
    {
        return createImporterService() //
                .createImporter() //
                .importAny(this.inputs) //
                .getImportedItems();
    }

    private ImporterService createImporterService()
    {
        final ImportSettings settings = ImportSettings.createDefault();
        final ImporterContext context = new ImporterContext(settings);
        final InitializingServiceLoader<ImporterFactory, ImporterContext> serviceLoader = InitializingServiceLoader
                .load(ImporterFactory.class, context);
        final ImporterService service = new ImporterService(
                new ImporterFactoryLoader(serviceLoader), settings);
        context.setImporterService(service);
        return service;
    }

    @Override
    public List<SpecificationItem> importItems(final ImportSettings settings)
    {
        return createImporterService() //
                .createImporter() //
                .importAny(this.inputs) //
                .getImportedItems();
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
    public void export(final List<SpecificationItem> items)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void export(final List<SpecificationItem> items, final ExportSettings settings)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void report(final Trace trace)
    {
        new ReportService().reportTraceToStdOut(trace, ReportSettings.createDefault());
    }

    @Override
    public void report(final Trace trace, final ReportSettings settings)
    {
        new ReportService().reportTraceToStdOut(trace, settings);
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath)
    {
        new ReportService().reportTraceToPath(trace, outputPath, ReportSettings.createDefault());
    }

    @Override
    public void reportToPath(final Trace trace, final Path outputPath,
            final ReportSettings settings)
    {
        new ReportService().reportTraceToPath(trace, outputPath, settings);
    }
}