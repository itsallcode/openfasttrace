package org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.itsallcode.matcher.auto.AutoMatcher.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SimpleLinePatternTest
{
    static Stream<Arguments> testCases()
    {
        return Stream.of(
                testCase("abc", "abc", List.of()),
                testCase("abc", "def", null),
                testCase("([0-9]+)", "123", List.of("123")),
                testCase("([0-9]+)(\\w+)", "123abc", List.of("123", "abc")),
                testCase("(?:[0-9]+)", "123", List.of()),
                testCase("[0-9]+", "123", List.of()),
                testCase("([0-9]+)", "abc", null));
    }

    private static Arguments testCase(final String pattern, final String line, final List<String> expected)
    {
        return Arguments.of(pattern, line, expected);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(final String pattern, final String line, final List<String> expected)
    {
        final Optional<List<String>> matches = SimpleLinePattern.of(pattern).getMatches(line, null);
        if (expected == null)
        {
            assertThat("Text '" + line + "' should not match pattern '" + pattern + "'", matches.isPresent(),
                    is(false));
        }
        else
        {
            assertAll(
                    () -> assertThat("Text '" + line + "' should match pattern '" + pattern + "'", matches.isPresent(),
                            is(true)),
                    () -> assertThat(matches.get(), equalTo(expected)));
        }
    }
}
