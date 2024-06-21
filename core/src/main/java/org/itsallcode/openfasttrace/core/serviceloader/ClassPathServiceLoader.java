package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A service loader that loads services from the classpath using a given
 * {@link ServiceOrigin}.
 */
final class ClassPathServiceLoader<T> implements Loader<T>
{
    private static final Logger LOGGER = Logger.getLogger(ClassPathServiceLoader.class.getName());
    private final ServiceOrigin serviceOrigin;
    private final ServiceLoader<T> serviceLoader;

    ClassPathServiceLoader(final ServiceOrigin serviceOrigin, final ServiceLoader<T> serviceLoader)
    {
        this.serviceOrigin = serviceOrigin;
        this.serviceLoader = serviceLoader;
    }

    static <T> Loader<T> create(final Class<T> serviceType, final ServiceOrigin serviceOrigin)
    {
        return new ClassPathServiceLoader<>(serviceOrigin,
                ServiceLoader.load(serviceType, serviceOrigin.getClassLoader()));
    }

    @Override
    public Stream<T> load()
    {
        return this.serviceLoader.stream().map(Provider::get)
                .filter(this::filterOtherClassLoader);
    }

    private boolean filterOtherClassLoader(final T service)
    {
        final ClassLoader serviceClassLoader = service.getClass().getClassLoader();
        final boolean correctClassLoader = serviceClassLoader == serviceOrigin.getClassLoader();
        if (!correctClassLoader)
        {
            LOGGER.fine(() -> "Service " + service + " has wrong class loader: " + serviceClassLoader + ", expected "
                    + serviceOrigin.getClassLoader());
        }
        return correctClassLoader;
    }

    @Override
    public void close()
    {
        this.serviceOrigin.close();
    }
}
