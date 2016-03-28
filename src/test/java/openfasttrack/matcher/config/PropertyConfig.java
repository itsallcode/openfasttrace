package openfasttrack.matcher.config;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.util.function.Function;

import org.hamcrest.Matcher;

/**
 * @param <T>
 *            base type of the {@link Matcher}
 * @param
 *            <P>
 *            type of the property
 */
class PropertyConfig<T, P>
{
    private final String propertyName;
    private final Matcher<P> matcher;
    private final Function<T, P> propertyAccessor;

    PropertyConfig(final String propertyName, final Matcher<P> matcher,
            final Function<T, P> propertyAccessor)
    {
        this.propertyName = propertyName;
        this.matcher = matcher;
        this.propertyAccessor = propertyAccessor;
    }

    public String getPropertyName()
    {
        return this.propertyName;
    }

    public Matcher<P> getMatcher()
    {
        return this.matcher;
    }

    public P getPropertyValue(final T object)
    {
        return this.propertyAccessor.apply(object);
    }
}