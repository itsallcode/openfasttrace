package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

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