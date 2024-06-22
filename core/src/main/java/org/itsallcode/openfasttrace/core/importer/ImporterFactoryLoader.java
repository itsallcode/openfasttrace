package org.itsallcode.openfasttrace.core.importer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.core.serviceloader.InitializingServiceLoader;
import org.itsallcode.openfasttrace.core.serviceloader.Loader;

/**
 * This class is responsible for finding the matching {@link ImporterFactory}
 * for a given {@link Path}.
 */
public class ImporterFactoryLoader
{
    private static final Logger LOG = Logger.getLogger(ImporterFactoryLoader.class.getName());

    private final Loader<ImporterFactory> serviceLoader;

    /**
     * Creates a new loader.
     * 
     * @param serviceLoader
     *            the loader used for locating importers.
     */
    ImporterFactoryLoader(final Loader<ImporterFactory> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Create a new loader for the given context.
     * 
     * @param context
     *            the context for the new loader.
     */
    public ImporterFactoryLoader(final ImporterContext context)
    {
        // [impl->dsn~plugins.loading.plugin_types~1]
        this(InitializingServiceLoader.load(ImporterFactory.class, context));
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
    public Optional<ImporterFactory> getImporterFactory(final InputFile file)
    {
        final List<ImporterFactory> matchingImporters = getMatchingFactories(file);
        switch (matchingImporters.size())
        {
        case 0:
            LOG.info(() -> "Found no matching importer for file '" + file + "'");
            return Optional.empty();
        case 1:
            return Optional.of(matchingImporters.get(0));
        default:
            LOG.info(() -> "Found more than one matching importer for file '" + file + "'");
            return Optional.empty();
        }
    }

    /**
     * Check if any {@link ImporterFactory} supports importing the given
     * {@link InputFile}.
     * 
     * @param file
     *            the file for which to check if an importer exists.
     * @return {@code true} if an importer exists, else {@code false}.
     */
    public boolean supportsFile(final InputFile file)
    {
        final boolean supported = !getMatchingFactories(file).isEmpty();
        LOG.finest(() -> "File " + file + " is supported = " + supported);
        return supported;
    }

    private List<ImporterFactory> getMatchingFactories(final InputFile file)
    {
        return this.serviceLoader.load()
                .filter(f -> f.supportsFile(file))
                .toList();
    }
}
