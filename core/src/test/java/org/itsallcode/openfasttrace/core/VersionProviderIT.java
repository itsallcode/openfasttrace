package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class VersionProviderIT
{
    private static final String CLASS_NAME = VersionProvider.class.getName();

    // [itest->dsn~cli.version~1]
    @Test
    void testLoadVersionFromProperties()
    {
        final String version = new VersionProvider().getVersion();
        assertAll(() -> assertThat(version, is(not("unknown"))),
                () -> assertThat(version, is(not("${version}"))));
    }

    /**
     * Test that the version provider returns "unknown" if the
     * version.properties resource is missing.
     * <p>
     * Uses a custom classloader to simulate a missing resource.
     * </p>
     */
    @EnumSource(ClassLoaderResourceLoadBehavior.class)
    @ParameterizedTest
    void testReturnUnknownIfResourceIsMissing(final ClassLoaderResourceLoadBehavior behavior) throws Exception
    {
        final byte[] classAsBytes = readClassIntoByteArray();
        final ClassLoader customLoader = new CustomClassLoader(VersionProvider.class.getClassLoader(), classAsBytes,
                behavior);
        final String version = invokeGetVersion(customLoader.loadClass(CLASS_NAME));
        assertThat(version, is("unknown"));
    }

    private byte @NonNull [] readClassIntoByteArray() throws IOException
    {
        final String classAsPath = CLASS_NAME.replace('.', '/') + ".class";
        final byte[] classAsBytes;
        try (InputStream is = VersionProvider.class.getClassLoader().getResourceAsStream(classAsPath))
        {
            classAsBytes = is.readAllBytes();
        }
        return classAsBytes;
    }

    private static String invokeGetVersion(final Class<?> clazz)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final Object provider = clazz.getDeclaredConstructor().newInstance();
        final Method getVersion = clazz.getMethod("getVersion");
        return (String) getVersion.invoke(provider);
    }

    private static final class CustomClassLoader extends ClassLoader
    {
        final String className = VersionProvider.class.getName();
        final byte[] classAsBytes;
        private final ClassLoaderResourceLoadBehavior behavior;

        public CustomClassLoader(ClassLoader parent, final byte[] classAsBytes,
                final ClassLoaderResourceLoadBehavior behavior)
        {
            super(parent);
            this.classAsBytes = classAsBytes;
            this.behavior = behavior;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException
        {
            if (name.equals(className))
            {
                return findClass(name);
            }
            return super.loadClass(name);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException
        {
            if (name.equals(className))
            {
                return defineClass(name, this.classAsBytes, 0, this.classAsBytes.length);
            }
            throw new ClassNotFoundException(name);
        }

        @Override
        public URL getResource(String name)
        {
            if (name.endsWith("version.properties"))
            {
                if (ClassLoaderResourceLoadBehavior.RETURN_NON_EXISTING_RESOURCE.equals(behavior))
                {
                    try
                    {
                        return new URI("file:///this/file/does/not/exists").toURL();
                    }
                    catch (MalformedURLException | URISyntaxException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    return null;
                }
            }
            return super.getResource(name);
        }
    }

    private enum ClassLoaderResourceLoadBehavior
    {
        RETURN_NULL, RETURN_NON_EXISTING_RESOURCE
    }
}
