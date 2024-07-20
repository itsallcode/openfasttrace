package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassPathServiceLoaderTest
{
    @Test
    void loadingNonAccessibleServiceFails()
    {
        final ServiceOrigin origin = ServiceOrigin.forCurrentClassPath();
        final ServiceConfigurationError error = assertThrows(ServiceConfigurationError.class,
                () -> ClassPathServiceLoader.create(DummyService.class, origin));
        assertThat(error.getMessage(), equalTo(
                "org.itsallcode.openfasttrace.core.serviceloader.ClassPathServiceLoaderTest$DummyService: module org.itsallcode.openfasttrace.core does not declare `uses`"));
    }

    @Test
    void loadingFindsNothing()
    {
        final List<ReporterFactory> services = ClassPathServiceLoader
                .create(ReporterFactory.class, ServiceOrigin.forCurrentClassPath()).load()
                .toList();
        assertThat(services, emptyIterable());
    }

    @Test
    void loadingReturnsService(@Mock final ServiceLoader<DummyService> serviceLoaderMock,
            @Mock final Provider<DummyService> providerMock, @Mock final ServiceOrigin originMock)
    {
        final DummyServiceImpl service = new DummyServiceImpl();
        when(serviceLoaderMock.stream()).thenReturn(Stream.of(providerMock));
        when(providerMock.get()).thenReturn(service);
        when(originMock.getClassLoader()).thenReturn(DummyServiceImpl.class.getClassLoader());
        final List<DummyService> services = new ClassPathServiceLoader<DummyService>(originMock, serviceLoaderMock)
                .load().toList();
        assertThat(services, contains(sameInstance(service)));
    }

    @Test
    void loadingIgnoresServicesFromOtherClassLoaders(@Mock final ServiceLoader<DummyService> serviceLoaderMock,
            @Mock final Provider<DummyService> providerMock, @Mock final ServiceOrigin originMock)
    {
        final DummyServiceImpl service = new DummyServiceImpl();
        when(serviceLoaderMock.stream()).thenReturn(Stream.of(providerMock));
        when(providerMock.get()).thenReturn(service);
        when(originMock.getClassLoader()).thenReturn(mock(ClassLoader.class));
        final List<DummyService> services = new ClassPathServiceLoader<DummyService>(originMock, serviceLoaderMock)
                .load().toList();
        assertThat(services, empty());
    }

    static interface DummyService
    {
    }

    static class DummyServiceImpl implements DummyService
    {
    }
}
