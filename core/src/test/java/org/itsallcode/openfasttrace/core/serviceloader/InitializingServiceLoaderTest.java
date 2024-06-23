package org.itsallcode.openfasttrace.core.serviceloader;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InitializingServiceLoaderTest
{
    @Mock
    Loader<ServiceMock> delegateMock;
    @Mock
    Object contextMock;
    @Mock
    ServiceMock serviceInstanceMock;

    @Test
    void initializesServices()
    {
        when(delegateMock.load()).thenReturn(Stream.of(serviceInstanceMock));
        try (Loader<ServiceMock> loader = new InitializingServiceLoader<>(delegateMock, contextMock))
        {
            loader.load();
        }
        verify(serviceInstanceMock).init(same(contextMock));
    }

    @Test
    void closesDelegate()
    {
        new InitializingServiceLoader<>(delegateMock, contextMock).close();
        verify(delegateMock).close();
    }

    private interface ServiceMock extends Initializable<Object>
    {

    }
}
