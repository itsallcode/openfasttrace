package org.itsallcode.openfasttrace.core.serviceloader;

import static java.util.stream.Collectors.joining;

import java.net.*;
import java.nio.file.Path;
import java.util.List;

final class ServiceOrigin
{
    private final ClassLoader classLoader;

    private ServiceOrigin(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public static ServiceOrigin forCurrentClassPath()
    {
        return new ServiceOrigin(getBaseClassLoader());
    }

    private static ClassLoader getBaseClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ServiceOrigin forJar(final Path jar)
    {
        return forJars(List.of(jar));
    }

    public static ServiceOrigin forJars(final List<Path> jars)
    {
        return new ServiceOrigin(createClassLoader(jars));
    }

    private static ClassLoader createClassLoader(final List<Path> jars)
    {
        final String name = "ServiceClassLoader-"
                + jars.stream().map(Path::getFileName).map(Path::toString).collect(joining(","));
        final URL[] urls = jars.stream().map(ServiceOrigin::toUrl).toArray(URL[]::new);
        return new URLClassLoader(name, urls, getBaseClassLoader());
    }

    private static URL toUrl(final Path path)
    {
        try
        {
            return path.toUri().toURL();
        }
        catch (final MalformedURLException e)
        {
            throw new IllegalStateException("Error converting path " + path + " to url", e);
        }
    }

    public ClassLoader getClassLoader()
    {
        return classLoader;
    }
}
