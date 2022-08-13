package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLegacySpecificationItemId
{
    private SpecificationItemId.Builder builder;

    @BeforeEach
    public void prepareEachTest()
    {
        this.builder = new SpecificationItemId.Builder();
    }

    @Test
    void testBuildInferingArtifactTypeFromShortNamePrefix()
    {
        final SpecificationItemId id = this.builder.name("foo:bar").revision(1).build();
        assertIdParts(id, "foo", "bar", 1);
    }

    private void assertIdParts(final SpecificationItemId id, final String expectedArtifactType,
            final String expectedName, final int expectedRevision)
    {
        assertThat("artifact type", id.getArtifactType(), equalTo(expectedArtifactType));
        assertThat("name", id.getName(), equalTo(expectedName));
        assertThat("revision", id.getRevision(), equalTo(expectedRevision));
    }

    @Test
    void testBuildInferingArtifactTypeFromLongNamePrefix()
    {
        final SpecificationItemId id = this.builder.name("foo~foo:bar").revision(1).build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    void testBuildnferingArtifactTypeWithoutPrefixResultsInUnknown()
    {
        final SpecificationItemId id = this.builder.name("bar").revision(1).build();
        assertIdParts(id, SpecificationItemId.UNKNOWN_ARTIFACT_TYPE, "bar", 1);
    }

    @Test
    void testBuildCleaningUpNameWherePrefixEqualsArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("foo").name("foo:bar").revision(1)
                .build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    void testBuildCleaningUpNameWhereLongPrefixEqualsArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("foo").name("foo~foo:bar")
                .revision(1).build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    void testBuildCleaningUpNameWherePrefixNotEqualArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("baz").name("foo~foo:bar")
                .revision(1).build();
        assertIdParts(id, "baz", "bar", 1);
    }

    @Test
    void testBuildInferingArtifactTypeAndCleaningUpNameWherePrefixPartsDontMatch()
    {
        final SpecificationItemId id = this.builder.name("baz~foo:bar").revision(1).build();
        assertIdParts(id, "baz", "bar", 1);
    }

    @Test
    void testParsingLegacyId()
    {
        final SpecificationItemId id = SpecificationItemId.parseId("req:dep-a, v1");
        assertIdParts(id, "req", "dep-a", 1);
    }
}
