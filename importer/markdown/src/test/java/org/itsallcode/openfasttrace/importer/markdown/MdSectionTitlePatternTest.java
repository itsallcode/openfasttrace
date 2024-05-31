package org.itsallcode.openfasttrace.importer.markdown;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MdSectionTitlePatternTest
{
    static Stream<Arguments> testCases()
    {
        return Stream.of(
                testCase(null, null, null),
                testCase(null, "ignored", null),
                testCase(null, "====", null),
                testCase("ignored", null, null),
                testCase("# Title", null, "Title"),
                testCase("## Title", null, "Title"),
                testCase("## Title with words", null, "Title with words"),
                testCase("##  \tLeading  whitespace removed ", null, "Leading  whitespace removed "),
                testCase("# Title", "ignored", "Title"),
                testCase("# Title", "=======", "Title"),
                testCase("Title with words", "=======", "Title with words"),
                testCase("\t Leading & trailing whitespace not removed ", "=======",
                        "\t Leading & trailing whitespace not removed "),
                underlineRecognized("======="),
                underlineRecognized("-------"),
                underlineRecognized("==="),
                underlineRecognized("---"),
                underlineNotRecognized("=="),
                underlineNotRecognized("--"),
                underlineNotRecognized("______"),
                underlineNotRecognized("^^^^^^"));
    }

    private static Arguments underlineRecognized(final String underline)
    {
        return Arguments.of("Title", underline, "Title");
    }

    private static Arguments underlineNotRecognized(final String underline)
    {
        return Arguments.of("Title", underline, null);
    }

    private static Arguments testCase(final String line, final String nextLine, final String expected)
    {
        return Arguments.of(line, nextLine, expected);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(final String line, final String nextLine, final String expected)
    {
        final MdSectionTitlePattern pattern = new MdSectionTitlePattern();
        final Optional<List<String>> result = pattern.getMatches(line, nextLine);
        if (expected == null)
        {
            assertThat("Lines '" + line + "' + '" + nextLine + "' should not be recognized as a section title",
                    result.isPresent(), is(false));
        }
        else
        {
            assertAll(() -> assertThat(
                    "Lines '" + line + "' + '" + nextLine + "' should be recognized as a section title",
                    result.isPresent(), is(true)), () -> assertThat(result.get().get(0), is(expected)));
        }
    }
}
