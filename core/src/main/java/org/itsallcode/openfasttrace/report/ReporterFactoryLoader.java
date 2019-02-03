package org.itsallcode.openfasttrace.report;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.itsallcode.openfasttrace.exporter.ExporterException;

/**
 * This class is responsible for finding the matching {@link ReporterFactory}
 * for a given format.
 */
public class ReporterFactoryLoader
{
    private final InitializingServiceLoader<ReporterFactory, ReporterContext> serviceLoader;

    public ReporterFactoryLoader(final ReporterContext context)
    {
        this(InitializingServiceLoader.load(ReporterFactory.class, context));
    }

    ReporterFactoryLoader(
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
                .filter(f -> f.supportsFormat(format)) //
                .collect(toList());
    }

    /**
     * Determines if the requested format is supported
     * 
     * @param format
     *            the requested exporter format
     * @return <code>true</code> if the format is supported
     */
    public boolean isFormatSupported(final String format)
    {
        return !getMatchingFactories(format).isEmpty();
    }
}
