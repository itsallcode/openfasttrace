package org.itsallcode.openfasttrace.core;

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
