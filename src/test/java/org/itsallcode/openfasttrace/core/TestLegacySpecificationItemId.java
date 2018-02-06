package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
