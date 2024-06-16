package org.itsallcode.openfasttrace.core.serviceloader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DelegatingLoaderTest
{
    @Test
    void noDelegates()
    {
        assertThat(new DelegatingLoader<>(List.of()).load().toList(), empty());
    }

    @Test
    void emptyDelegate(@Mock final Loader<DummyService> loaderMock)
    {
        when(loaderMock.load()).thenReturn(Stream.empty());
        assertThat(new DelegatingLoader<>(List.of(loaderMock)).load().toList(), empty());
    }

    @Test
    void nonEmptyDelegate(@Mock final Loader<DummyService> loaderMock, @Mock final DummyService serviceMock)
    {
        when(loaderMock.load()).thenReturn(Stream.of(serviceMock));
        assertThat(new DelegatingLoader<>(List.of(loaderMock)).load().toList(), contains(serviceMock));
    }

    @Test
    void loadsDelegatesInOrder(@Mock final Loader<DummyService> loaderMock1,
            @Mock final Loader<DummyService> loaderMock2, @Mock final DummyService serviceMock1,
            @Mock final DummyService serviceMock2)
    {
        when(loaderMock1.load()).thenReturn(Stream.of(serviceMock1));
        when(loaderMock2.load()).thenReturn(Stream.of(serviceMock2));
        assertThat(new DelegatingLoader<>(List.of(loaderMock1, loaderMock2)).load().toList(),
                contains(serviceMock1, serviceMock2));
    }

    static interface DummyService
    {
    }
}
