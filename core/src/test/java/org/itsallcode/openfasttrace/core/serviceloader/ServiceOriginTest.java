package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.net.URLClassLoader;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// [utest->dsn~plugins.loading~1]
@ExtendWith(MockitoExtension.class)
class ServiceOriginTest
{
    @Test
    void closesUrlClassLoader(@Mock final URLClassLoader classLoaderMock)
            throws Exception
    {
        final ServiceOrigin serviceOrigin = new ServiceOrigin.JarFileOrigin(Collections.emptyList(), classLoaderMock);
        serviceOrigin.close();
        verify(((AutoCloseable) classLoaderMock)).close();
        verifyNoMoreInteractions(classLoaderMock);
    }

    @Test
    void currentClassPathOriginToString()
    {
        final ServiceOrigin origin = ServiceOrigin.forCurrentClassPath();
        assertThat(origin, hasToString(startsWith("CurrentClassPathOrigin [classLoader=")));
    }

    // [utest->dsn~plugins.loading.separate_classloader~1]
    @Test
    void currentClassPathGetClassLoader()
    {
        final ServiceOrigin origin = ServiceOrigin.forCurrentClassPath();
        assertThat(origin.getClassLoader(), sameInstance(Thread.currentThread().getContextClassLoader()));
    }

    @Test
    void currentClassPathClose()
    {
        final ServiceOrigin origin = ServiceOrigin.forCurrentClassPath();
        assertDoesNotThrow(origin::close);
    }

    @Test
    void jarFileOriginToString()
    {
        final ServiceOrigin origin = ServiceOrigin.forJar(ClassPathHelper.findJarForClass(Test.class));
        assertThat(origin,
                hasToString(allOf(startsWith("JarFileOrigin [classLoader="), containsString("junit-jupiter-api"))));
    }

    @Test
    void jarFileOriginGetClassLoader()
    {
        final ServiceOrigin origin = ServiceOrigin.forJar(ClassPathHelper.findJarForClass(Test.class));
        assertThat(origin.getClassLoader(), instanceOf(URLClassLoader.class));
    }

    @Test
    void jarFileOriginClose()
    {
        final ServiceOrigin origin = ServiceOrigin.forJar(ClassPathHelper.findJarForClass(Test.class));
        assertDoesNotThrow(origin::close);
    }
}
