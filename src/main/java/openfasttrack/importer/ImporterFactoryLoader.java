package openfasttrack.importer;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * This class is responsible for finding the matching {@link ImporterFactory}
 * for a given {@link Path}.
 */
public class ImporterFactoryLoader
{
    private final ServiceLoader<ImporterFactory> serviceLoader;

    public ImporterFactoryLoader()
    {
        this(ServiceLoader.load(ImporterFactory.class));
    }

    ImporterFactoryLoader(final ServiceLoader<ImporterFactory> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Finds a matching {@link ImporterFactory} that can handle the given
     * {@link Path}. If no or more than one {@link ImporterFactory} is found,
     * this throws an {@link ImporterException}.
     *
     * @param file
     *            the file for wich to get a {@link ImporterFactory}.
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
                .filter((f) -> f.supportsFile(file)) //
                .collect(toList());
    }
}
