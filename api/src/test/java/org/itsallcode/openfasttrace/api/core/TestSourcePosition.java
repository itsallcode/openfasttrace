package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSourcePosition
{
    @Test
    void testExposesZeroBasedLineAndColumn()
    {
        final SourcePosition position = new SourcePosition(4, 7);

        assertAll(
                () -> assertThat(position.getLine(), equalTo(4)),
                () -> assertThat(position.getColumn(), equalTo(7)),
                () -> assertThat(position.toString(), equalTo("4:7")));
    }

    @Test
    void testRejectsNegativeLine()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SourcePosition(-1, 0));

        assertThat(exception.getMessage(), equalTo("Source positions must not be negative"));
    }

    @Test
    void testRejectsNegativeColumn()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SourcePosition(0, -1));

        assertThat(exception.getMessage(), equalTo("Source positions must not be negative"));
    }

    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(SourcePosition.class).verify();
    }
}
