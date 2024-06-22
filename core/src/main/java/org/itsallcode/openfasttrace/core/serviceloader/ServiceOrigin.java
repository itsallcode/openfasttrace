package org.itsallcode.openfasttrace.core.serviceloader;

import static java.util.stream.Collectors.joining;

import java.net.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Origin of a service, either the current classpath or a list of jar files.
 */
interface ServiceOrigin extends AutoCloseable
{
    /**
     * Create a service origin for the current classpath.
     * 
     * @return a service origin for the current classpath
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
     * Create a service origin for a single jar file.
     * 
     * @param jar
     *            path to the jar file
     * @return a service origin for the jar file
     */
    static ServiceOrigin forJar(final Path jar)
    {
        return forJars(List.of(jar));
    }

    /**
     * Create a service origin for a list of jar files.
     * 
     * @param jars
     *            list of paths to jar files
     * @return a service origin for the jar files
     */
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

    static final class JarFileOrigin implements ServiceOrigin
    {
        private final List<Path> jars;
        private final URLClassLoader classLoader;

        JarFileOrigin(final List<Path> jars, final URLClassLoader classLoader)
        {
            this.jars = jars;
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
            catch (final Exception exception)
            {
                throw new IllegalStateException("Error closing class loader " + classLoader, exception);
            }
        }

        @Override
        public String toString()
        {
            return "JarFileOrigin [classLoader=" + classLoader + ", jars=" + jars + "]";
        }
    }

    static final class CurrentClassPathOrigin implements ServiceOrigin
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
            return "CurrentClassPathOrigin [classLoader=" + classLoader + "]";
        }
    }
}
