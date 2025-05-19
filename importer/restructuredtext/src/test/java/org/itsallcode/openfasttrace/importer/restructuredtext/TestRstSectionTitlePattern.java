package org.itsallcode.openfasttrace.importer.restructuredtext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestRstSectionTitlePattern
{

    static Stream<Arguments> testCases()
    {
        return Stream.of(
                titleNotRecongnized(null, null),
                titleNotRecongnized(null, "ignored"),
                titleNotRecongnized(null, "===="),
                titleNotRecongnized("ignored", null),
                titleNotRecongnized("# Title", null),
                titleNotRecongnized("## Title", null),
                titleNotRecongnized("# Title", "ignored"),
                testCase("# Title", "=======", "# Title"),
                testCase("Title with words", "=======", "Title with words"),
                testCase("\t Leading & trailing whitespace not removed ", "=======",
                        "\t Leading & trailing whitespace not removed "),
                underlineRecognized("==="),
                underlineRecognized("---"),
                underlineRecognized("___"),
                underlineRecognized(":::"),
                underlineRecognized("..."),
                underlineRecognized("^^^"),
                underlineRecognized("```"),
                underlineRecognized("\"\"\""),
                underlineRecognized("'''"),
                underlineRecognized("^^^"),
                underlineRecognized("~~~"),
                underlineRecognized("***"),
                underlineRecognized("+++"),
                underlineRecognized("###"),
                underlineRecognized("<<<"),
                underlineRecognized(">>>"),
                underlineRecognized("======="),
                underlineNotRecognized("=="),
                underlineNotRecognized("--"),
                underlineNotRecognized("__"),
                underlineNotRecognized("^^"));
    }

    private static Arguments underlineRecognized(final String underline)
    {
        return Arguments.of("Title", underline, "Title");
    }

    private static Arguments underlineNotRecognized(final String underline)
    {
        return Arguments.of("Title", underline, null);
    }

    private static Arguments titleNotRecongnized(final String line, final String nextLine)
    {
        return testCase(line, nextLine, null);
    }

    private static Arguments testCase(final String line, final String nextLine, final String expected)
    {
        return Arguments.of(line, nextLine, expected);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void test(final String line, final String nextLine, final String expected)
    {
        final RstSectionTitlePattern pattern = new RstSectionTitlePattern();
        final Optional<List<String>> result = pattern.getMatches(line, nextLine);
        if (expected == null)
        {
            assertThat("Lines '" + line + "' + '" + nextLine + "' should not be recognized as a section title",
                    result.isPresent(), is(false));
        }
        else
        {
            if(result.isPresent()) {
                final List<String> matches = result.get();
                assertAll(
                        () -> assertThat(matches, hasSize(1)),
                        () -> assertThat(matches.get(0), is(expected))
                );
            } else {
                fail("No match found for '" + line + "' + '" + nextLine + "'");
            }
        }
    }
}
