package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
                () -> assertThat(range.toString(), equalTo("2:3 … 4:5")));
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

        assertThat(exception.getMessage(), equalTo("start must not be null"));
    }

    @Test
    void testRejectsNullEnd()
    {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new SourceRange(START, null));

        assertThat(exception.getMessage(), equalTo("end must not be null"));
    }

    @ParameterizedTest
    @CsvSource(
    { "4, 5, 2, 3", "2, 4, 2, 3", "2, 3, 2, 2" })
    void testRejectsEndPrecedingStart(final int startLine, final int startColumn, final int endLine,
            final int endColumn)
    {
        final SourcePosition start = new SourcePosition(startLine, startColumn);
        final SourcePosition end = new SourcePosition(endLine, endColumn);

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SourceRange(start, end));

        assertThat(exception.getMessage(),
                equalTo("The range end " + endLine + ":" + endColumn + " must not precede its start " + startLine + ":"
                        + startColumn));
    }

    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(SourceRange.class).verify();
    }
}
