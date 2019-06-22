package org.itsallcode.openfasttrace.api.core;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.core.Location;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestLocation
{
    private static final String PATH = "path";

    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(Location.class).verify();
    }

    @Test
    void testCreateLocationWithNegativeLineFails()
    {
        assertThrows(IllegalArgumentException.class, () -> Location.create(PATH, -1));
    }

    @Test
    void testCreateLocationWithNegativeLineFails2()
    {
        assertThrows(IllegalArgumentException.class, () -> Location.create(PATH, -1, 1));
    }

    @Test
    void testCreateLocationWithNegativeColumnFails()
    {
        assertThrows(IllegalArgumentException.class, () -> Location.create(PATH, 1, -1));
    }

    @Test
    void testBuilder()
    {
        final String expectedPath = "expected";
        final int expectedLine = 4007;
        final int expectedColumn = 10203;
        final Location location = Location.builder().path(expectedPath).line(expectedLine)
                .column(expectedColumn).build();
        assertThat(location.getPath(), equalTo(expectedPath));
        assertThat(location.getLine(), equalTo(expectedLine));
        assertThat(location.getColumn(), equalTo(expectedColumn));
    }

    @Test
    void testIsComplete()
    {
        final Location.Builder builder = Location.builder().path("a");
        assertThat("builder with path set to null is complete", builder.isCompleteEnough(),
                equalTo(true));
    }

    @Test
    void testIsNotCompleteEnoughWithoutPath()
    {
        final Location.Builder builderA = Location.builder().path("");
        final Location.Builder builderB = Location.builder();
        assertThat("builder with empty path is complete", builderA.isCompleteEnough(),
                equalTo(false));
        assertThat("builder with path set to null is complete", builderB.isCompleteEnough(),
                equalTo(false));
    }

    @Test
    void testToStringWithAllComponents()
    {
        final Location location = Location.builder().path("foo").line(1).column(2).build();
        assertThat(location.toString(), equalTo("foo:1:2"));

    }

    @Test
    void testToStringWithFileAndLine()
    {
        final Location location = Location.builder().path("foo").line(1).build();
        assertThat(location.toString(), equalTo("foo:1"));

    }

    @Test
    void testToStringWithFile()
    {
        final Location location = Location.builder().path("foo").build();
        assertThat(location.toString(), equalTo("foo"));

    }
}