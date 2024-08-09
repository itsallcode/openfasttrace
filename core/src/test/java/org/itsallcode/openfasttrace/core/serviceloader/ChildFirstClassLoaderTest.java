package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URL;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

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
        final URL jarForClass = ClassPathHelper.findUrlForClass(classToFind);
        try (final ChildFirstClassLoader testee = new ChildFirstClassLoader("name", new URL[] { jarForClass },
                Thread.currentThread().getContextClassLoader()))
        {
            final Class<?> foundClass = testee.loadClass(classToFind.getName());
            assertThat(foundClass, not(sameInstance(classToFind)));
            assertThat(foundClass.toGenericString(), equalTo(classToFind.toGenericString()));
        }
    }

    /**
     * We can't stub the ClassLoader using Mockito because method
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
