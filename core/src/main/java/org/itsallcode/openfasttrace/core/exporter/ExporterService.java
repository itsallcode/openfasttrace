package org.itsallcode.openfasttrace.core.exporter;

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
