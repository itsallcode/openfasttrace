package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PatternConstantsTest
{
    @ParameterizedTest
    @ValueSource(strings = { "req", "feat", "DSN", "Artifact" })
    void testArtifactTypeMatchesValid(final String input)
    {
        assertThat(input.matches(PatternConstants.ARTIFACT_TYPE), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "req_1", "feat-item", "" })
    void testArtifactTypeRejectsInvalid(final String input)
    {
        assertThat(input.matches(PatternConstants.ARTIFACT_TYPE), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = { "+", "*", "-" })
    void testBulletsMatchesValid(final String input)
    {
        assertThat(input.matches(PatternConstants.BULLETS), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "/", "#", "=", "++", "" })
    void testBulletsRejectsInvalid(final String input)
    {
        assertThat(input.matches(PatternConstants.BULLETS), is(false));
    }

    // [utest->dsn~md.tags-format~1]
    @ParameterizedTest
    @ValueSource(strings = { "tag", "TAG_1", "0_valid", "a" })
    void testTagPatternMatchesValid(final String input)
    {
        assertThat(input.matches(PatternConstants.TAG_PATTERN), is(true));
    }

    // [utest->dsn~md.tags-format~1]
    @ParameterizedTest
    @ValueSource(strings = { "_invalid", "-invalid", "tag with spaces", "" })
    void testTagPatternRejectsInvalid(final String input)
    {
        assertThat(input.matches(PatternConstants.TAG_PATTERN), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "  ", "   " })
    void testUpTo3WhitespacesMatchesValid(final String input)
    {
        assertThat(input.matches(PatternConstants.UP_TO_3_WHITESPACES), is(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "    ", "\t\t\t\t" })
    void testUpTo3WhitespacesRejectsMoreThan3(final String input)
    {
        assertThat(input.matches(PatternConstants.UP_TO_3_WHITESPACES), is(false));
    }

    @ParameterizedTest
    @ValueSource(strings = { "* req~name~1", "  * req~name~1", "   * req~name~1", "+ req~name~1", "- req~name~1",
            "* covers: req~name~1", "* req~name~1 extra" })
    void testReferenceAfterBulletMatchesValid(final String input)
    {
        // Given/When/Then: input matches the pattern and captures the ID
        final Matcher matcher = Pattern.compile(PatternConstants.REFERENCE_AFTER_BULLET).matcher(input);
        assertAll(() -> assertThat(matcher.matches(), is(true)),
                () -> assertThat(matcher.group(1), equalTo("req~name~1")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "    * req~name~1", "req~name~1", "*", "  *  " })
    void testReferenceAfterBulletRejectsInvalid(final String input)
    {
        // Given/When/Then: input does not match
        assertThat(Pattern.compile(PatternConstants.REFERENCE_AFTER_BULLET).matcher(input).matches(), is(false));
    }
}
