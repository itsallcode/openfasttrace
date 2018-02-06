package org.itsallcode.openfasttrace.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TestLegacySpecificationItemId
{
    private SpecificationItemId.Builder builder;

    @Before
    public void prepareEachTest()
    {
        this.builder = new SpecificationItemId.Builder();
    }

    @Test
    public void testBuildInferingArtifactTypeFromShortNamePrefix()
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
    public void testBuildInferingArtifactTypeFromLongNamePrefix()
    {
        final SpecificationItemId id = this.builder.name("foo~foo:bar").revision(1).build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    public void testBuildnferingArtifactTypeWithoutPrefixResultsInUnknown()
    {
        final SpecificationItemId id = this.builder.name("bar").revision(1).build();
        assertIdParts(id, SpecificationItemId.UNKONWN_ARTIFACT_TYPE, "bar", 1);
    }

    @Test
    public void testBuildCleaningUpNameWherePrefixEqualsArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("foo").name("foo:bar").revision(1)
                .build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    public void testBuildCleaningUpNameWhereLongPrefixEqualsArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("foo").name("foo~foo:bar")
                .revision(1).build();
        assertIdParts(id, "foo", "bar", 1);
    }

    @Test
    public void testBuildCleaningUpNameWherePrefixNotEqualArtifactType()
    {
        final SpecificationItemId id = this.builder.artifactType("baz").name("foo~foo:bar")
                .revision(1).build();
        assertIdParts(id, "baz", "bar", 1);
    }

    @Test
    public void testBuildInferingArtifactTypeAndCleaningUpNameWherePrefixPartsDontMatch()
    {
        final SpecificationItemId id = this.builder.name("baz~foo:bar").revision(1).build();
        assertIdParts(id, "baz", "bar", 1);
    }

    @Test
    public void testParsingLegacyId()
    {
        final SpecificationItemId id = SpecificationItemId.parseId("req:dep-a, v1");
        assertIdParts(id, "req", "dep-a", 1);
    }
}
