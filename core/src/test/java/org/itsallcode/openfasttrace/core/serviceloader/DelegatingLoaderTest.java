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
        assertThat(load(List.of()), empty());
    }

    @Test
    void emptyDelegate(@Mock final Loader<DummyService> loaderMock)
    {
        when(loaderMock.load()).thenReturn(Stream.empty());
        assertThat(load(List.of(loaderMock)), empty());
    }

    @Test
    void nonEmptyDelegate(@Mock final Loader<DummyService> loaderMock, @Mock final DummyService serviceMock)
    {
        when(loaderMock.load()).thenReturn(Stream.of(serviceMock));
        assertThat(load(List.of(loaderMock)), contains(serviceMock));
    }

    @Test
    void loadsDelegatesInOrder(@Mock final Loader<DummyService> loaderMock1,
            @Mock final Loader<DummyService> loaderMock2, @Mock final DummyService serviceMock1,
            @Mock final DummyService serviceMock2)
    {
        when(loaderMock1.load()).thenReturn(Stream.of(serviceMock1));
        when(loaderMock2.load()).thenReturn(Stream.of(serviceMock2));
        assertThat(load(List.of(loaderMock1, loaderMock2)),
                contains(serviceMock1, serviceMock2));
    }

    private <T> List<T> load(final List<Loader<T>> delegates)
    {
        try (DelegatingLoader<T> loader = new DelegatingLoader<T>(delegates))
        {
            return loader.load().toList();
        }
    }

    static interface DummyService
    {
    }
}
