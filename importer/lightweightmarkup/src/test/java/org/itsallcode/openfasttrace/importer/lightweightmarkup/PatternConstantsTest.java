package org.itsallcode.openfasttrace.importer.lightweightmarkup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.testutil.matcher.PatternMatcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PatternConstantsTest
{
    @ParameterizedTest
    @ValueSource(strings = { "req", "feat", "DSN", "Artifact" })
    void testArtifactTypeMatchesValid(final String input)
    {
        assertThat(PatternConstants.ARTIFACT_TYPE, PatternMatcher.patternMatches(input));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "req_1", "feat-item", "" })
    void testArtifactTypeRejectsInvalid(final String input)
    {
        assertThat(PatternConstants.ARTIFACT_TYPE, not(PatternMatcher.patternMatches(input)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "+", "*", "-" })
    void testBulletsMatchesValid(final String input)
    {
        assertThat(PatternConstants.BULLETS, PatternMatcher.patternMatches(input));
    }

    @ParameterizedTest
    @ValueSource(strings = { "/", "#", "=", "++", "" })
    void testBulletsRejectsInvalid(final String input)
    {
        assertThat(PatternConstants.BULLETS, not(PatternMatcher.patternMatches(input)));
    }

    // [utest->dsn~md.tags-format~1]
    @ParameterizedTest
    @ValueSource(strings = { "tag", "TAG_1", "0_valid", "a" })
    void testTagPatternMatchesValid(final String input)
    {
        assertThat(PatternConstants.TAG_PATTERN, PatternMatcher.patternMatches(input));
    }

    // [utest->dsn~md.tags-format~1]
    @ParameterizedTest
    @ValueSource(strings = { "_invalid", "-invalid", "tag with spaces", "" })
    void testTagPatternRejectsInvalid(final String input)
    {
        assertThat(PatternConstants.TAG_PATTERN, not(PatternMatcher.patternMatches(input)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "  ", "   " })
    void testUpTo3WhitespacesMatchesValid(final String input)
    {
        assertThat(PatternConstants.UP_TO_3_WHITESPACES, PatternMatcher.patternMatches(input));
    }

    @ParameterizedTest
    @ValueSource(strings = { "    ", "\t\t\t\t" })
    void testUpTo3WhitespacesRejectsMoreThan3(final String input)
    {
        assertThat(PatternConstants.UP_TO_3_WHITESPACES, not(PatternMatcher.patternMatches(input)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "* req~name~1", "  * req~name~1", "   * req~name~1", "+ req~name~1", "- req~name~1",
            "* covers: req~name~1", "* req~name~1 extra" })
    void testReferenceAfterBulletMatchesValid(final String input)
    {
        final Matcher matcher = Pattern.compile(PatternConstants.REFERENCE_AFTER_BULLET).matcher(input);
        assertAll(() -> assertThat(matcher.matches(), is(true)),
                () -> assertThat(matcher.group(1), equalTo("req~name~1")));
    }

    @ParameterizedTest
    @ValueSource(strings = { "    * req~name~1", "req~name~1", "*", "  *  " })
    void testReferenceAfterBulletRejectsInvalid(final String input)
    {
        assertThat(PatternConstants.REFERENCE_AFTER_BULLET, not(PatternMatcher.patternMatches(input)));
    }
}
