package org.itsallcode.openfasttrace.core;

import org.itsallcode.openfasttrace.core.Location;

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

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TestLocation
{
    private static final String PATH = "path";

    @Test
    public void equalsContract()
    {
        EqualsVerifier.forClass(Location.class).verify();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLocationWithNegativeLineFails()
    {
        Location.create(PATH, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLocationWithNegativeLineFails2()
    {
        Location.create(PATH, -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateLocationWithNegativeColumnFails()
    {
        Location.create(PATH, 1, -1);
    }
}
