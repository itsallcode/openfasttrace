package org.itsallcode.openfasttrace.report.plaintext;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.itsallcode.openfasttrace.report.plaintext.AnsiSequence.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AnsiSequenceTest {
    public static Stream<Arguments> getAnsiSequenceIds() {
        return Stream.of(
                Arguments.of(RESET, 0),
                Arguments.of(BOLD, 1),
                Arguments.of(ITALIC, 3),
                Arguments.of(UNDERLINE, 4),
                Arguments.of(INVERSE, 7),
                Arguments.of(BLACK, 30),
                Arguments.of(RED, 31),
                Arguments.of(GREEN, 32),
                Arguments.of(YELLOW, 33),
                Arguments.of(BLUE, 34),
                Arguments.of(MAGENTA, 35),
                Arguments.of(CYAN, 36),
                Arguments.of(WHITE, 37),
                Arguments.of(BRIGHT_RED, 91)
        );
    }

    @MethodSource("getAnsiSequenceIds")
    @ParameterizedTest
    void testValues(final AnsiSequence sequence, final int id) {
        assertThat(sequence.getId(), equalTo(id));
    }
}