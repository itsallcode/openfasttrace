package org.itsallcode.openfasttrace.importer;

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

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.StreamSupport;

import org.itsallcode.openfasttrace.core.ServiceLoaderWrapper;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * This class is responsible for finding the matching {@link ImporterFactory}
 * for a given {@link Path}.
 */
public class ImporterFactoryLoader
{
    private static final Logger LOG = Logger.getLogger(ImporterFactoryLoader.class.getName());

    private final ServiceLoaderWrapper<ImporterFactory> serviceLoader;
    private final ImporterContext context;

    public ImporterFactoryLoader(final ImporterContext context)
    {
        this(ServiceLoaderWrapper.load(ImporterFactory.class), context);
    }

    ImporterFactoryLoader(final ServiceLoaderWrapper<ImporterFactory> serviceLoader,
            final ImporterContext context)
    {
        this.serviceLoader = serviceLoader;
        this.context = context;
    }

    /**
     * Finds a matching {@link ImporterFactory} that can handle the given
     * {@link Path}. If no or more than one {@link ImporterFactory} is found,
     * this throws an {@link ImporterException}.
     *
     * @param file
     *            the file for which to get a {@link ImporterFactory}.
     * @return a matching {@link ImporterFactory} that can handle the given
     *         {@link Path}
     * @throws ImporterException
     *             when no or more than one {@link ImporterFactory} is found.
     */
    public ImporterFactory getImporterFactory(final InputFile file)
    {
        final List<ImporterFactory> matchingImporters = getMatchingFactories(file);
        switch (matchingImporters.size())
        {
        case 0:
            throw new ImporterException("Found no matching importer for file '" + file + "'");
        case 1:
            return matchingImporters.get(0);
        default:
            throw new ImporterException(
                    "Found more than one matching importer for file '" + file + "'");
        }
    }

    public boolean supportsFile(final InputFile file)
    {
        final boolean supported = !getMatchingFactories(file).isEmpty();
        LOG.finest(() -> "File " + file + " is supported = " + supported);
        return supported;
    }

    private List<ImporterFactory> getMatchingFactories(final InputFile file)
    {
        return StreamSupport.stream(this.serviceLoader.spliterator(), false) //
                .peek(service -> service.init(this.context)).filter(f -> f.supportsFile(file)) //
                .collect(toList());
    }
}
