package org.itsallcode.openfasttrace.core.serviceloader;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class ServiceOrigin implements AutoCloseable
{
    private final ClassLoader classLoader;
    private final List<Path> jars;

    private ServiceOrigin(final ClassLoader classLoader, final List<Path> jars)
    {
        this.classLoader = classLoader;
        this.jars = new ArrayList<>(jars);
    }

    public static ServiceOrigin forCurrentClassPath()
    {
        return new ServiceOrigin(getBaseClassLoader(), emptyList());
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
        return new ServiceOrigin(createClassLoader(jars), jars);
    }

    private static ClassLoader createClassLoader(final List<Path> jars)
    {
        final URL[] urls = jars.stream().map(ServiceOrigin::toUrl)
                .toArray(URL[]::new);
        return new ChildFirstClassLoader(getClassLoaderName(jars), urls, getBaseClassLoader());
    }

    private static String getClassLoaderName(final List<Path> jars)
    {
        return "ServiceClassLoader-"
                + jars.stream().map(Path::getFileName).sorted().map(Path::toString).collect(joining(","));
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

    @Override
    public String toString()
    {
        return "ServiceOrigin [classLoader=" + classLoader + ", jars=" + jars + "]";
    }

    public void close()
    {
        if (classLoader instanceof AutoCloseable)
        {
            try
            {
                ((AutoCloseable) classLoader).close();
            }
            catch (final Exception e)
            {
                throw new IllegalStateException("Error closing class loader " + classLoader, e);
            }
        }
    }
}
