package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;

final class ClassPathServiceLoader<T> implements Loader<T>
{
    private final ServiceLoader<T> serviceLoader;

    private ClassPathServiceLoader(final ServiceLoader<T> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    static <T> Loader<T> load(final Class<T> serviceType, final ServiceOrigin serviceOrigin)
    {
        return new ClassPathServiceLoader<>(ServiceLoader.load(serviceType, serviceOrigin.getClassLoader()));
    }

    @Override
    public Stream<T> load()
    {
        return this.serviceLoader.stream().map(Provider::get);
    }
}
