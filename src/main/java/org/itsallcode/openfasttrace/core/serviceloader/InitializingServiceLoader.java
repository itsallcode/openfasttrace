package org.itsallcode.openfasttrace.core.serviceloader;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.util.*;

/**
 * This service loader is similar to Java's {@link ServiceLoader} but
 * additionally initializes services with a given context.
 * 
 * @param <C>
 *            the context type.
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
     * @param serviceType
     *            the type of the services to load.
     * @param context
     *            the context with which to initialize the newly created service
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
        final Iterator<T> iterator = this.serviceLoader.iterator();
        while (iterator.hasNext())
        {
            final T service = iterator.next();
            service.init(this.context);
            initializedServices.add(service);
        }
        return initializedServices;
    }
}
