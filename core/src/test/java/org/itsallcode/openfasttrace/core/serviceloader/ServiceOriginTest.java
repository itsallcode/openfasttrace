package org.itsallcode.openfasttrace.core.serviceloader;

import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceOriginTest
{
    @Test
    void closesClassLoader(@Mock(extraInterfaces = AutoCloseable.class) final ClassLoader classLoaderMock)
            throws Exception
    {
        final ServiceOrigin serviceOrigin = new ServiceOrigin(classLoaderMock, Collections.emptyList());
        serviceOrigin.close();
        verify(((AutoCloseable) classLoaderMock)).close();
        verifyNoMoreInteractions(classLoaderMock);
    }

    @Test
    void doesNotCloseClassLoader(@Mock final ClassLoader classLoaderMock)
    {
        final ServiceOrigin serviceOrigin = new ServiceOrigin(classLoaderMock, Collections.emptyList());
        serviceOrigin.close();
        verifyNoInteractions(classLoaderMock);
    }
}
