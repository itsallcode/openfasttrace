package org.itsallcode.openfasttrace.core.serviceloader;
import java.util.*;

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
public class InitializingServiceLoader<T extends Initializable<C>, C> implements Iterable<T>
{
    private final ServiceLoader<T> serviceLoader;
    private final C context;
    private List<T> services;

    private InitializingServiceLoader(final ServiceLoader<T> serviceLoader, final C context)
    {
        this.serviceLoader = serviceLoader;
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
        return new InitializingServiceLoader<>(ServiceLoader.load(serviceType), context);
    }

    @Override
    public Iterator<T> iterator()
    {
        if (this.services == null)
        {
            this.services = loadServices();
        }
        return this.services.iterator();
    }

    private List<T> loadServices()
    {
        final List<T> initializedServices = new ArrayList<>();
        for (final T service : this.serviceLoader)
        {
            service.init(this.context);
            initializedServices.add(service);
        }
        return initializedServices;
    }
}
