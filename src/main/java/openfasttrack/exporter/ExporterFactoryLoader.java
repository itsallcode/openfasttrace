package openfasttrack.exporter;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import openfasttrack.core.ServiceLoaderWrapper;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.ImporterFactory;

/**
 * This class is responsible for finding the matching {@link ImporterFactory}
 * for a given {@link Path}.
 */
public class ExporterFactoryLoader
{
    private final ServiceLoaderWrapper<ExporterFactory> serviceLoader;

    public ExporterFactoryLoader()
    {
        this(ServiceLoaderWrapper.load(ExporterFactory.class));
    }

    ExporterFactoryLoader(final ServiceLoaderWrapper<ExporterFactory> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Finds a matching {@link ExporterFactory} that can handle the given output
     * format. If no or more than one {@link ExporterFactory} is found, this
     * throws an {@link ExporterException}.
     *
     * @param outputFormat
     *            the output format for which to get a {@link ImporterFactory}.
     * @return a matching {@link ExporterFactory} that can handle the given
     *         output format
     * @throws ImporterException
     *             when no or more than one {@link ExporterFactory} is found.
     */
    public ExporterFactory getExporterFactory(final String outputFormat)
    {
        final List<ExporterFactory> matchingExporters = getMatchingFactories(outputFormat);
        switch (matchingExporters.size())
        {
        case 0:
            throw new ImporterException(
                    "Found no matching exporter for output format '" + outputFormat + "'");
        case 1:
            return matchingExporters.get(0);
        default:
            throw new ImporterException("Found more than one matching exporter for output format '"
                    + outputFormat + "'");
        }
    }

    private List<ExporterFactory> getMatchingFactories(final String format)
    {
        return StreamSupport.stream(this.serviceLoader.spliterator(), false) //
                .filter(f -> f.supportsFormat(format)) //
                .collect(toList());
    }
}
