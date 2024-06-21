package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;

class ChildFirstClassLoaderTest
{
    @Test
    void parentFindsClass() throws ClassNotFoundException, IOException
    {
        try (final ChildFirstClassLoader testee = new ChildFirstClassLoader("name", new URL[] {},
                new MockClassLoader(String.class)))
        {
            assertThat(testee.loadClass("ClassName"), sameInstance(String.class));
        }
    }

    @Test
    void parentDoesNotFindClass() throws IOException
    {
        try (final ChildFirstClassLoader testee = new ChildFirstClassLoader("name", new URL[] {},
                new MockClassLoader(null)))
        {
            final ClassNotFoundException exception = assertThrows(ClassNotFoundException.class,
                    () -> testee.loadClass("ClassName"));
            assertThat(exception.getMessage(), equalTo("ClassName"));
        }
    }

    @Test
    void classFoundInJar() throws ClassNotFoundException, IOException
    {
        final Class<?> classToFind = Matchers.class;
        final URL jarForClass = findJarForClass(classToFind.getName());
        try (final ChildFirstClassLoader testee = new ChildFirstClassLoader("name", new URL[] { jarForClass },
                Thread.currentThread().getContextClassLoader()))
        {
            final Class<?> foundClass = testee.loadClass(classToFind.getName());
            assertThat(foundClass, not(sameInstance(classToFind)));
            assertThat(foundClass.toGenericString(), equalTo(classToFind.toGenericString()));
        }
    }

    public static URL findJarForClass(final String className) throws IOException
    {
        final String classAsResource = className.replace('.', '/') + ".class";
        final Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(classAsResource);
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
                return path.toUri().toURL();
            }
        }
        throw new AssertionError("No jar found containing " + className);
    }

    /**
     * We can't mock the ClassLoader using Mockito because method
     * {@link ClassLoader#loadClass(String, boolean)} is protected.
     */
    private static class MockClassLoader extends ClassLoader
    {
        private final Class<?> clazz;

        private MockClassLoader(final Class<?> clazz)
        {
            this.clazz = clazz;
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
        {
            if (clazz == null)
            {
                throw new ClassNotFoundException("mock");
            }
            return clazz;
        }
    }
}
