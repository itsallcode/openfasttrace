package org.itsallcode.openfasttrace.core.serviceloader;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Origin of a service, either the current class path or a list of JAR files.
 */
// [impl->dsn~plugins.loading~1]
interface ServiceOrigin extends AutoCloseable
{
    /**
     * Create a service origin for the current class path.
     * 
     * @return a service origin for the current class path
     */
    static ServiceOrigin forCurrentClassPath()
    {
        return new CurrentClassPathOrigin(getBaseClassLoader());
    }

    private static ClassLoader getBaseClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Create a service origin for a single JAR file.
     * 
     * @param jar
     *            path to the JAR file
     * @return a service origin for the JAR file
     */
    static ServiceOrigin forJar(final Path jar)
    {
        return forJars(List.of(jar));
    }

    /**
     * Create a service origin for a list of JAR files.
     * 
     * @param jars
     *            list of paths to JAR files
     * @return a service origin for the JAR files
     */
    // [impl->dsn~plugins.loading.separate-classloader~1]
    static ServiceOrigin forJars(final List<Path> jars)
    {
        return new JarFileOrigin(jars, createClassLoader(jars));
    }

    private static URLClassLoader createClassLoader(final List<Path> jars)
    {
        final URL[] urls = jars.stream().map(ServiceOrigin::toUrl)
                .toArray(URL[]::new);
        return new ChildFirstClassLoader(getClassLoaderName(jars), urls, getBaseClassLoader());
    }

    private static String getClassLoaderName(final List<Path> jars)
    {
        return "JarClassLoader-"
                + jars.stream().map(Path::getFileName).sorted().map(Path::toString).collect(joining(","));
    }

    private static URL toUrl(final Path path)
    {
        try
        {
            return path.toUri().toURL();
        }
        catch (final MalformedURLException exception)
        {
            throw new IllegalStateException("Error converting path " + path + " to url", exception);
        }
    }

    /**
     * Get the class loader for this service origin.
     * 
     * @return the class loader for this service origin
     */
    ClassLoader getClassLoader();

    /**
     * Close the underlying ClassLoader if appropriate.
     */
    void close();

    final class JarFileOrigin implements ServiceOrigin
    {
        private final List<Path> jars;
        private final URLClassLoader classLoader;

        JarFileOrigin(final List<Path> jars, final URLClassLoader classLoader)
        {
            this.jars = new ArrayList<>(jars);
            this.classLoader = classLoader;
        }

        @Override
        public ClassLoader getClassLoader()
        {
            return classLoader;
        }

        @Override
        public void close()
        {
            try
            {
                classLoader.close();
            }
            catch (final IOException exception)
            {
                throw new UncheckedIOException("Error closing class loader " + classLoader, exception);
            }
        }

        @Override
        public String toString()
        {
            return "JarFileOrigin [classLoader=" + classLoader.getName() + ", jars=" + jars + "]";
        }
    }

    final class CurrentClassPathOrigin implements ServiceOrigin
    {
        private final ClassLoader classLoader;

        CurrentClassPathOrigin(final ClassLoader classLoader)
        {
            this.classLoader = classLoader;
        }

        @Override
        public ClassLoader getClassLoader()
        {
            return classLoader;
        }

        @Override
        public void close()
        {
            // The current class loader cannot be closed.
        }

        @Override
        public String toString()
        {
            return "CurrentClassPathOrigin [classLoader=" + classLoader.getName() + "]";
        }
    }
}
