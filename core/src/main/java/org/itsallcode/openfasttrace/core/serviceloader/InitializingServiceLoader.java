package org.itsallcode.openfasttrace.core.serviceloader;

import java.util.*;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;

/**
 * This service loader is similar to Java's {@link ServiceLoader} but
 * additionally initializes services with a given context.
 * 
 * @param <T>
 *            initializable object
 * @param <C>
 *            the context type
 */
public final class InitializingServiceLoader<T extends Initializable<C>, C> implements Iterable<T>, Loader<T>
{
    private final Loader<T> delegate;
    private final C context;
    private List<T> services;

    private InitializingServiceLoader(final Loader<T> delegate, final C context)
    {
        this.delegate = delegate;
        this.context = context;
    }

    /**
     * Create a new {@link InitializingServiceLoader}.
     * 
     * @param <T>
     *            service type.
     * @param <C>
     *            service context type.
     * @param serviceType
     *            type of the services to load.
     * @param context
     *            context with which to initialize the newly created service
     *            instances.
     * @return an {@link InitializingServiceLoader} for type <code>T</code>
     */
    public static <T extends Initializable<C>, C> InitializingServiceLoader<T, C> load(
            final Class<T> serviceType, final C context)
    {
        final ServiceLoaderFactory loaderFactory = ServiceLoaderFactory.createDefault();
        return new InitializingServiceLoader<>(loaderFactory.createLoader(serviceType), context);
    }

    @Override
    public Stream<T> load()
    {
        if (this.services == null)
        {
            this.services = loadServices();
        }
        return services.stream();
    }

    private List<T> loadServices()
    {
        return this.delegate.load().map(service -> {
            service.init(this.context);
            return service;
        }).toList();
    }

    @Override
    public Iterator<T> iterator()
    {
        return load().iterator();
    }
}
