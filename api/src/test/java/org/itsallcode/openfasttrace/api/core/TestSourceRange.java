package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSourceRange
{
    private static final SourcePosition START = new SourcePosition(2, 3);
    private static final SourcePosition END = new SourcePosition(4, 5);

    @Test
    void testExposesStartAndEndPositions()
    {
        final SourceRange range = new SourceRange(START, END);

        assertAll(
                () -> assertThat(range.getStart(), equalTo(START)),
                () -> assertThat(range.getEnd(), equalTo(END)),
                () -> assertThat(range.toString(), equalTo("2:3-4:5")));
    }

    @Test
    void testAcceptsEmptyAndMultiLineRanges()
    {
        final SourcePosition position = new SourcePosition(2, 3);

        assertAll(
                () -> assertThat(new SourceRange(position, position).getEnd(), equalTo(position)),
                () -> assertThat(new SourceRange(new SourcePosition(2, 5), new SourcePosition(3, 0)).getEnd(),
                        equalTo(new SourcePosition(3, 0))));
    }

    @Test
    void testRejectsNullStart()
    {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new SourceRange(null, END));

        assertThat(exception.getMessage(), equalTo("start"));
    }

    @Test
    void testRejectsNullEnd()
    {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new SourceRange(START, null));

        assertThat(exception.getMessage(), equalTo("end"));
    }

    @Test
    void testRejectsEndOnEarlierLine()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SourceRange(END, START));

        assertThat(exception.getMessage(), equalTo("The range end must not precede its start"));
    }

    @Test
    void testRejectsEndOnEarlierColumn()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SourceRange(new SourcePosition(2, 4), new SourcePosition(2, 3)));

        assertThat(exception.getMessage(), equalTo("The range end must not precede its start"));
    }

    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(SourceRange.class).verify();
    }
}
