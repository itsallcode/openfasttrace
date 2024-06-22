package org.itsallcode.openfasttrace.api.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.openfasttrace.api.core.SpecificationItemId.createId;
import static org.itsallcode.openfasttrace.api.core.SpecificationItemId.parseId;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * [utest->dsn~md.specification-item-id-format~3]
 */
// [utest->dsn~specification-item-id~1]
class TestSpecificationItemId
{
    @ParameterizedTest(name = "Parsing of ID ''{0}'' succeeds")
    @CsvSource(value =
    {
            "type~name~42, type, name, 42",
            "type~name-with-dash~42, type, name-with-dash, 42",
            "type~name.with.dot~42, type, name.with.dot, 42",
            "type~name-trailing-~42, type, name-trailing-, 42",
            "foobar~utf-8.compatible.äöü~333, foobar, utf-8.compatible.äöü, 333",
            // Deprecated Elektrobit-style specification item ID
            "'feat:foo, v1', feat, foo, 1",
            "req~ab1~2, req, ab1, 2", // Name ends with a number
            "req~ab.1~2, req, ab.1, 2", // Numbers directly after dot
            "req~ab__1~2, req, ab__1, 2", // Consecutive underscores
            "req~ab--1~2, req, ab--1, 2", // Consecutive dashes
            // ID with multiple dots
            "req~SR.AB.a.b.c~2, req, SR.AB.a.b.c, 2",
            "req~SR.AB.1.1.1~2, req, SR.AB.1.1.1, 2",
            "req~SR.AB.1_1_1~2, req, SR.AB.1_1_1, 2",
            "req~SR.AB.a_b_c~2, req, SR.AB.a_b_c, 2",
    })
    void parsingValidIdsSucceeds(final String id, final String expectedType, final String expectedName,
            final int expectedRevision)
    {
        final SpecificationItemId parsedId = parseId(id);
        assertAll(
                () -> assertThat(parsedId.getArtifactType(), equalTo(expectedType)),
                () -> assertThat(parsedId.getName(), equalTo(expectedName)),
                () -> assertThat(parsedId.getRevision(), equalTo(expectedRevision)));
    }

    // This is the wildcard revision test
    @Test
    void testCreateIdWithoutRevision()
    {
        final SpecificationItemId id = createId("impl", "wildcard-feature");
        assertAll(() -> assertThat(id, equalTo(new Builder().artifactType("impl").name("wildcard-feature")
                .revision(Integer.MIN_VALUE).build())),
                () -> assertThat(SpecificationItemId.REVISION_WILDCARD, equalTo(Integer.MIN_VALUE)));
    }

    @Test
    void testParseId_IllegalNumberFormat()
    {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> parseId("feat~foo~999999999999999999999999999999999999999"));
        assertThat(exception.getMessage(), equalTo(
                "Error parsing version number from specification item ID: \"feat~foo~999999999999999999999999999999999999999\""));
    }

    @CsvSource({
            "feat.foo~1",
            "foo~1", // missing name
            "req~foo", // missing revision
            "req1~foo~1", // type ends with number
            "1req~abc~2", // type with leading number
            "re1q~abc~2", // type contains number
            "req.r~foo~1", // type contain a dot
            "req~1foo~1", // name starts with number
            "req~.foo~1", // name begins with a dot
            "req~foo.~1", // name ends with a dot
            "req~_foo~1", // name starts with underscore
            "req~-foo~1", // name starts with dash
            "req~foo~-1", // negative revision
            "req~foo..a~0", // consecutive dots
    })
    @ParameterizedTest(name = "Parsing of id ''{0}'' fails")
    void testParseId_mustFailForIllegalIds(final String illegalId)
    {
        final Throwable exception = assertThrows(IllegalArgumentException.class, () -> parseId(illegalId));
        assertThat(exception.getMessage(),
                equalTo("Invalid specification item ID format: \"" + illegalId + "\". "
                        + "Format must be <arifact-type>~<requirement-name-or-number>~<revision>."));
    }

    @Test
    void testToRevisionWildcard()
    {
        final SpecificationItemId id = parseId("feat~foo~999");
        assertThat(id.toRevisionWildcard().getRevision(),
                equalTo(SpecificationItemId.REVISION_WILDCARD));
    }

    @Test
    void testToString()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revision(3);
        assertThat(builder.build().toString(), equalTo("dsn~dummy~3"));
    }

    @Test
    void testToStringRevisionWildcard()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revisionWildcard();
        assertThat(builder.build().toString(), equalTo("dsn~dummy~" + SpecificationItemId.REVISION_WILDCARD));
    }

    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(SpecificationItemId.class).verify();
    }
}
