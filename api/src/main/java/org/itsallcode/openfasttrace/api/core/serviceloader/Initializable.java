package org.itsallcode.openfasttrace.api.core.serviceloader;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

/**
 * Interface for services that are initialized with a context after
 * instantiation.
 *
 * @param <C>
 *            the context injected via {@link #init(Object)}.
 */
public interface Initializable<C>
{
    /**
     * Initializes the service with the given context.
     * 
     * @param context
     *            the context object with which to initialize the service
     *            object.
     */
    void init(C context);
}
