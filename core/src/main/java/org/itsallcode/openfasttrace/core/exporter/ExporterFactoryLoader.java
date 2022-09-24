package org.itsallcode.openfasttrace.core.exporter;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.api.exporter.*;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;

/**
 * This class is responsible for finding the matching {@link ExporterFactory}
 * for a given {@link Path}.
 */
public class ExporterFactoryLoader
{
    private final InitializingServiceLoader<ExporterFactory, ExporterContext> serviceLoader;

    /**
     * Create a new loader for the given context.
     * 
     * @param context
     *            the context for the new loader.
     */
    public ExporterFactoryLoader(final ExporterContext context)
    {
        this(InitializingServiceLoader.load(ExporterFactory.class, context));
    }

    ExporterFactoryLoader(
            final InitializingServiceLoader<ExporterFactory, ExporterContext> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Finds a matching {@link ExporterFactory} that can handle the given output
     * format. If no or more than one {@link ExporterFactory} is found, this
     * throws an {@link ExporterException}.
     *
     * @param outputFormat
     *            the output format for which to get a {@link ExporterFactory}.
     * @return a matching {@link ExporterFactory} that can handle the given
     *         output format
     * @throws ExporterException
     *             when no or more than one {@link ExporterFactory} is found.
     */
    public ExporterFactory getExporterFactory(final String outputFormat)
    {
        final List<ExporterFactory> matchingExporters = getMatchingFactories(outputFormat);
        switch (matchingExporters.size())
        {
        case 0:
            throw new ExporterException(
                    "Found no matching exporter for output format '" + outputFormat + "'");
        case 1:
            return matchingExporters.get(0);
        default:
            throw new ExporterException("Found more than one matching exporter for output format '"
                    + outputFormat + "'");
        }
    }

    private List<ExporterFactory> getMatchingFactories(final String format)
    {
        return StreamSupport.stream(this.serviceLoader.spliterator(), false) //
                .filter(f -> f.supportsFormat(format)) //
                .collect(toList());
    }

    /**
     * Determines if the requested format is supported
     * 
     * @param format
     *            the requested exporter format
     * @return {@code true} if the format is supported
     */
    public boolean isFormatSupported(final String format)
    {
        return !getMatchingFactories(format).isEmpty();
    }
}
