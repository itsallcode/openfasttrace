package org.itsallcode.openfasttrace.core.serviceloader;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

import org.junit.jupiter.api.condition.OS;

final class ClassPathHelper
{
    static URL findUrlForClass(final Class<?> clazz)
    {
        return findUrlForClass(clazz.getName());
    }

    static URL findUrlForClass(final String className)
    {
        try
        {
            return findJarForClass(className).toUri().toURL();
        }
        catch (final MalformedURLException exception)
        {
            throw new IllegalStateException("Error creating URL for class " + className, exception);
        }
    }

    static Path findJarForClass(final Class<?> clazz)
    {
        return findJarForClass(clazz.getName());
    }

    static Path findJarForClass(final String className)
    {
        final String resourceName = className.replace('.', '/') + ".class";
        final Enumeration<URL> urls = getResources(resourceName);
        while (urls.hasMoreElements())
        {
            final URL url = urls.nextElement();
            if ("jar".equals(url.getProtocol()))
            {
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                if (OS.WINDOWS.isCurrentOs())
                {
                    // Remove leading slash of "/C:/Users/user/.m2/..."
                    jarPath = jarPath.substring(1);
                }
                final Path path = Path.of(jarPath);
                assertTrue(Files.exists(path));
                return path;
            }
        }
        throw new AssertionError("No jar found containing " + className);
    }

    private static Enumeration<URL> getResources(final String resourceName)
    {
        try
        {
            return Thread.currentThread().getContextClassLoader().getResources(resourceName);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException("Error finding resource " + resourceName, exception);
        }
    }
}
