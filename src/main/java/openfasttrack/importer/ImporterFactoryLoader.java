package openfasttrack.importer;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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
import java.util.stream.StreamSupport;

import openfasttrack.core.ServiceLoaderWrapper;

/**
 * This class is responsible for finding the matching {@link ImporterFactory}
 * for a given {@link Path}.
 */
public class ImporterFactoryLoader
{
    private final ServiceLoaderWrapper<ImporterFactory> serviceLoader;

    public ImporterFactoryLoader()
    {
        this(ServiceLoaderWrapper.load(ImporterFactory.class));
    }

    ImporterFactoryLoader(final ServiceLoaderWrapper<ImporterFactory> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
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
    public ImporterFactory getImporterFactory(final Path file)
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

    private List<ImporterFactory> getMatchingFactories(final Path file)
    {
        return StreamSupport.stream(this.serviceLoader.spliterator(), false) //
                .filter(f -> f.supportsFile(file)) //
                .collect(toList());
    }
}
