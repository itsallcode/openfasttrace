package org.itsallcode.openfasttrace.core.serviceloader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class PluginLoaderFactory
{
    private final Path pluginsDirectory;

    PluginLoaderFactory(final Path pluginsDirectory)
    {
        this.pluginsDirectory = pluginsDirectory;
    }

    static PluginLoaderFactory createDefault()
    {
        return new PluginLoaderFactory(getHomeDirectory().resolve(".oft").resolve("plugins"));
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
        final List<Loader<T>> loaders = origins.stream().map(origin -> ClassPathServiceLoader.load(serviceType, origin))
                .toList();
        return new DelegatingLoader<>(loaders);
    }

    private List<ServiceOrigin> findServiceOrigins()
    {
        final List<ServiceOrigin> origins = new ArrayList<>();
        origins.add(ServiceOrigin.forCurrentClassPath());
        origins.addAll(findPluginOrigins());
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
            return Optional.of(ServiceOrigin.forJars(List.of(path)));
        }
        if (!Files.isDirectory(path))
        {
            return Optional.empty();
        }
        final List<Path> jars = findJarsInDir(path);
        if (jars.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(ServiceOrigin.forJars(jars));
    }

    private static List<Path> findJarsInDir(final Path path)
    {
        try
        {
            return Files.list(path).filter(PluginLoaderFactory::isJarFile).toList();
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
