package openfasttrack.core;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * This is a non-final wrapper for {@link ServiceLoader}. It is required as
 * final classes cannot be mocked in unit tests.
 */
public class ServiceLoaderWrapper<T> implements Iterable<T>
{
    private final ServiceLoader<T> serviceLoader;

    private ServiceLoaderWrapper(final ServiceLoader<T> serviceLoader)
    {
        this.serviceLoader = serviceLoader;
    }

    public static <T> ServiceLoaderWrapper<T> load(final Class<T> service)
    {
        return new ServiceLoaderWrapper<>(ServiceLoader.load(service));
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.serviceLoader.iterator();
    }
}