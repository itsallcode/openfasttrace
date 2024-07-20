package org.itsallcode.openfasttrace.core.serviceloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Factory for creating {@link Loader} instances for services.
 */
class ServiceLoaderFactory
{
    private static final Logger LOGGER = Logger.getLogger(ServiceLoaderFactory.class.getName());
    private final Path pluginsDirectory;
    private final boolean searchCurrentClasspath;

    /**
     * Create a new factory for service {@link Loader}.
     * 
     * @param pluginsDirectory
     *            directory to search for plugins
     * @param searchCurrentClasspath
     *            whether to search the current classpath for plugins. This is
     *            useful for testing to avoid loading plugins twice.
     */
    ServiceLoaderFactory(final Path pluginsDirectory, final boolean searchCurrentClasspath)
    {
        this.pluginsDirectory = pluginsDirectory;
        this.searchCurrentClasspath = searchCurrentClasspath;
    }

    /**
     * Create a default factory that searches for plugins in the user's home.
     * 
     * @return a default factory
     */
    static ServiceLoaderFactory createDefault()
    {
        final Path pluginsDirectory = getHomeDirectory().resolve(".oft").resolve("plugins");
        return new ServiceLoaderFactory(pluginsDirectory, true);
    }

    private static Path getHomeDirectory()
    {
        return Path.of(System.getProperty("user.home"));
    }

    /**
     * Create a new {@link Loader} for the given service type.
     * 
     * @param <T>
     *            service type
     * @param serviceType
     *            service type
     * @return a new {@link Loader} for the given service type
     */
    <T> Loader<T> createLoader(final Class<T> serviceType)
    {
        return createLoader(serviceType, findServiceOrigins());
    }

    private static <T> Loader<T> createLoader(final Class<T> serviceType, final List<ServiceOrigin> origins)
    {
        final List<Loader<T>> loaders = origins.stream()
                .map(origin -> ClassPathServiceLoader.create(serviceType, origin))
                .toList();
        return new DelegatingLoader<>(loaders);
    }

    /**
     * Find all service origins.
     * <p>
     * This method is package-private for testing.
     * 
     * @return a list of service origins
     */
    // [impl->dsn~plugins.loading~1]
    List<ServiceOrigin> findServiceOrigins()
    {
        final List<ServiceOrigin> origins = new ArrayList<>(findPluginOrigins());
        if (searchCurrentClasspath)
        {
            origins.add(ServiceOrigin.forCurrentClassPath());
        }
        LOGGER.finest(() -> "Found " + origins.size() + " service origins: " + origins + ".");
        return origins;
    }

    private Collection<ServiceOrigin> findPluginOrigins()
    {
        if (!Files.isDirectory(pluginsDirectory))
        {
            return Collections.emptyList();
        }
        try
        {
            try (Stream<Path> stream = Files.list(pluginsDirectory))
            {
                return stream.sorted()
                        .map(ServiceLoaderFactory::originForPath)
                        .flatMap(Optional::stream)
                        .toList();
            }
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(
                    "Failed to list plugin directories in '" + this.pluginsDirectory + "': " + exception.getMessage(),
                    exception);
        }
    }

    private static Optional<ServiceOrigin> originForPath(final Path path)
    {
        if (isJarFile(path))
        {
            LOGGER.fine(() -> "Found single plugin JAR '" + path + "'.");
            return Optional.of(ServiceOrigin.forJars(List.of(path)));
        }
        if (!Files.isDirectory(path))
        {
            LOGGER.fine(() -> "Ignoring plugin search path '" + path + "' because it is not a directory.");
            return Optional.empty();
        }
        final List<Path> jars = findJarsInDir(path);
        if (jars.isEmpty())
        {
            LOGGER.fine(() -> "Ignoring empty plugin directory '" + path + "'.");
            return Optional.empty();
        }
        LOGGER.fine(() -> "Found " + jars.size() + " JAR files in '" + path + "': " + jars + ".");
        return Optional.of(ServiceOrigin.forJars(jars));
    }

    private static List<Path> findJarsInDir(final Path path)
    {
        try (Stream<Path> stream = Files.list(path))
        {
            return stream.filter(ServiceLoaderFactory::isJarFile).toList();
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(
                    "Failed to list files in plugin directory '" + path + "': " + exception.getMessage(),
                    exception);
        }
    }

    private static boolean isJarFile(final Path path)
    {
        return path.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".jar");
    }
}
