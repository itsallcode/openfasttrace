package openfasttrack.exporter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import openfasttrack.core.LinkedSpecificationItem;

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
     * Export the given {@link LinkedSpecificationItem} in the given output
     * format to a file
     *
     * @param items
     *            the {@link LinkedSpecificationItem} to export
     * @param outputFormat
     *            the output format
     * @param outputFile
     *            the output file
     */
    public void exportFile(final List<LinkedSpecificationItem> items, final String outputFormat,
            final Path outputFile)
    {
        final ExporterFactory factory = this.factoryLoader.getExporterFactory(outputFormat);
        factory.createExporter(outputFile, outputFormat, StandardCharsets.UTF_8, items).runExport();
    }
}
