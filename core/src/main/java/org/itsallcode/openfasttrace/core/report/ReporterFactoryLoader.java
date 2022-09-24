package org.itsallcode.openfasttrace.core.report;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.api.report.*;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;

/**
 * This class is responsible for finding the matching {@link ReporterFactory}
 * for a given format.
 */
public class ReporterFactoryLoader
{
    private final InitializingServiceLoader<ReporterFactory, ReporterContext> serviceLoader;

    /**
     * Create a new {@link ReporterFactoryLoader}.
     * 
     * @param context
     *            the context used for initializing new
     *            {@link ReporterFactory}s.
     */
    public ReporterFactoryLoader(final ReporterContext context)
    {
        this(InitializingServiceLoader.load(ReporterFactory.class, context));
    }

    private ReporterFactoryLoader(
            final InitializingServiceLoader<ReporterFactory, ReporterContext> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Finds a matching {@link ReporterFactory} that can handle the given output
     * format. If no or more than one {@link ReporterFactory} is found, this
     * throws an {@link ReportException}.
     *
     * @param outputFormat
     *            the output format for which to get a {@link ReporterFactory}.
     * @return a matching {@link ReporterFactory} that can handle the given
     *         output format
     * @throws ReportException
     *             when no or more than one {@link ReporterFactory} is found.
     */
    public ReporterFactory getReporterFactory(final String outputFormat)
    {
        final List<ReporterFactory> matchingReporters = getMatchingFactories(outputFormat);
        switch (matchingReporters.size())
        {
        case 0:
            throw new ExporterException(
                    "Found no matching reporter for output format '" + outputFormat + "'");
        case 1:
            return matchingReporters.get(0);
        default:
            throw new ReportException("Found more than one matching reporter for output format '"
                    + outputFormat + "'");
        }
    }

    private List<ReporterFactory> getMatchingFactories(final String format)
    {
        return StreamSupport.stream(this.serviceLoader.spliterator(), false) //
                .filter(factory -> factory.supportsFormat(format)) //
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
