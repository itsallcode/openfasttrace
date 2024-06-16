package org.itsallcode.openfasttrace.core.serviceloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

class ServiceLoaderFactory
{
    private static final Logger LOGGER = Logger.getLogger(ServiceLoaderFactory.class.getName());
    private final Path pluginsDirectory;
    private final boolean searchCurrentClasspath;

    /**
     * Create a new factory for service {@link Loader}.
     * 
     * @param pluginsDirectory
     *            the directory to search for plugins
     * @param searchCurrentClasspath
     *            whether to search the current classpath for plugins. This is
     *            useful for testing to avoid loading plugins twice.
     */
    ServiceLoaderFactory(final Path pluginsDirectory, final boolean searchCurrentClasspath)
    {
        this.pluginsDirectory = pluginsDirectory;
        this.searchCurrentClasspath = searchCurrentClasspath;
    }

    static ServiceLoaderFactory createDefault()
    {
        return new ServiceLoaderFactory(getHomeDirectory().resolve(".oft").resolve("plugins"), true);
    }

    private static Path getHomeDirectory()
    {
        return Path.of(System.getProperty("user.home"));
    }

    <T> Loader<T> createLoader(final Class<T> serviceType)
    {
        return createLoader(serviceType, findServiceOrigins());
    }

    private <T> Loader<T> createLoader(final Class<T> serviceType, final List<ServiceOrigin> origins)
    {
        final List<Loader<T>> loaders = origins.stream()
                .map(origin -> ClassPathServiceLoader.create(serviceType, origin))
                .toList();
        return new DelegatingLoader<>(loaders);
    }

    List<ServiceOrigin> findServiceOrigins()
    {
        final List<ServiceOrigin> origins = new ArrayList<>();
        if (searchCurrentClasspath)
        {
            origins.add(ServiceOrigin.forCurrentClassPath());
        }
        origins.addAll(findPluginOrigins());
        LOGGER.fine(() -> "Found " + origins.size() + " service origins: " + origins + ".");
        return origins;
    }

    Collection<ServiceOrigin> findPluginOrigins()
    {
        if (!Files.isDirectory(pluginsDirectory))
        {
            return Collections.emptyList();
        }
        try
        {
            return Files.list(pluginsDirectory)
                    .sorted()
                    .map(this::originForPath)
                    .flatMap(Optional::stream)
                    .toList();
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(
                    "Failed to list plugin directories in '" + this.pluginsDirectory + "': " + exception.getMessage(),
                    exception);
        }
    }

    private Optional<ServiceOrigin> originForPath(final Path path)
    {
        if (isJarFile(path))
        {
            LOGGER.fine(() -> "Found single plugin jar '" + path + "'.");
            return Optional.of(ServiceOrigin.forJars(List.of(path)));
        }
        if (!Files.isDirectory(path))
        {
            LOGGER.fine(() -> "Ignore unsupported file '" + path + "'.");
            return Optional.empty();
        }
        final List<Path> jars = findJarsInDir(path);
        if (jars.isEmpty())
        {
            LOGGER.fine(() -> "Ignore empty plugin directory '" + path + "'.");
            return Optional.empty();
        }
        LOGGER.fine(() -> "Found " + jars.size() + " in '" + path + "': " + jars + ".");
        return Optional.of(ServiceOrigin.forJars(jars));
    }

    private static List<Path> findJarsInDir(final Path path)
    {
        try
        {
            return Files.list(path).filter(ServiceLoaderFactory::isJarFile).toList();
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
