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
import static org.itsallcode.openfasttrace.core.SpecificationItemId.createId;
import static org.itsallcode.openfasttrace.core.SpecificationItemId.parseId;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.itsallcode.openfasttrace.core.SpecificationItemId.Builder;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * [utest->dsn~md.specification-item-id-format~2]
 */
// [utest->dsn~specification-item-id~1]
public class TestSpecificationItemId
{
    private static final int REVISION = 1;
    private static final String NAME = "foo";
    private static final String ARTIFACT_TYPE_FEATURE = "feat";

    @Test
    public void testCreateId()
    {
        final SpecificationItemId id = createId(ARTIFACT_TYPE_FEATURE, NAME, REVISION);
        assertThat(id, equalTo(new Builder().artifactType(ARTIFACT_TYPE_FEATURE).name(NAME)
                .revision(REVISION).build()));
    }

    @Test
    public void testCreateIdWithoutRevision()
    {
        final SpecificationItemId id = createId(ARTIFACT_TYPE_FEATURE, NAME);
        assertThat(id, equalTo(new Builder().artifactType(ARTIFACT_TYPE_FEATURE).name(NAME)
                .revision(Integer.MIN_VALUE).build()));
    }

    @Test
    public void testParseId_singleDigitRevision()
    {
        final SpecificationItemId id = parseId("feat~foo~1");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleFragmentName()
    {
        final SpecificationItemId id = parseId("feat~foo.bar_zoo.baz-narf~1");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo("foo.bar_zoo.baz-narf"));
        assertThat(id.getRevision(), equalTo(1));
    }

    @Test
    public void testParseId_multipleDigitRevision()
    {
        final SpecificationItemId id = parseId("feat~foo~999");
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(999));
    }

    @Test(expected = IllegalStateException.class)
    public void testParseIdFailsForWildcardRevision()
    {
        parseId("feat~foo~" + SpecificationItemId.REVISION_WILDCARD);
    }

    @Test
    public void testParseId_mustFailForIllegalIds()
    {
        final String[] negatives = { "feat.foo~1", "foo~1", "req~foo", "req1~foo~1", "req.r~foo~1",
                "req~1foo~1", "req~.foo~1", "req~foo~-1" };

        for (final String sample : negatives)
        {
            assertParsingExceptionOnIllegalSpecificationItemId(sample);
        }
    }

    private void assertParsingExceptionOnIllegalSpecificationItemId(final String sample)
    {
        try
        {
            parseId(sample);
            fail("Expected exception trying to parse \"" + sample
                    + "\" into a specification item ID");
        }
        catch (final IllegalStateException exception)
        {
            // this block intentionally left empty
        }
    }

    @Test
    public void testToRevisionWildcard()
    {
        final SpecificationItemId id = parseId("feat~foo~999");
        assertThat(id.toRevisionWildcard().getRevision(),
                equalTo(SpecificationItemId.REVISION_WILDCARD));
    }

    @Test
    public void testToString()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revision(3);
        assertThat(builder.build().toString(), equalTo("dsn~dummy~3"));
    }

    @Test
    public void testToStringRevisionWildcard()
    {
        final Builder builder = new Builder();
        builder.artifactType("dsn").name("dummy").revisionWildcard();
        assertThat(builder.build().toString(),
                equalTo("dsn~dummy~" + SpecificationItemId.REVISION_WILDCARD));
    }

    public void testCreate_WithRevisionWildcard()
    {
        final SpecificationItemId id = createId(ARTIFACT_TYPE_FEATURE, NAME);
        assertThat(id.getArtifactType(), equalTo(ARTIFACT_TYPE_FEATURE));
        assertThat(id.getName(), equalTo(NAME));
        assertThat(id.getRevision(), equalTo(SpecificationItemId.REVISION_WILDCARD));
    }

    @Test
    public void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(SpecificationItemId.class).verify();
    }
}
