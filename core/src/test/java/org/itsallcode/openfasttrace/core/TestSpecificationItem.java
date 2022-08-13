package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.matcher.SpecificationItemIdMatcher;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSpecificationItem
{
    private static final String NOT_NEEDED_ARTIFACT_TYPE = "not_needed";
    private static final String NEEDED_ARTIFACT_TYPE = "needed";
    final static String ARTIFACT_TYPE = "req";
    final static String NAME = "foobar";
    final static int REVISION = 1;
    final static String ID_AS_TEXT = ARTIFACT_TYPE + SpecificationItemId.ARTIFACT_TYPE_SEPARATOR
            + NAME + SpecificationItemId.REVISION_SEPARATOR + REVISION;
    final static SpecificationItemId ID = SpecificationItemId.parseId(ID_AS_TEXT);
    final static String DESCRIPTION = "This is a description\nwith multiple lines";
    final static String RATIONALE = "A rationale\nwith multiple lines";
    final static String COMMENT = "A comment\nwith multiple lines";
    final static List<SpecificationItemId> COVERED_IDS = parseIds("feat~foo~1", "feat~bar~2",
            "constr~baz~3");
    final static List<String> NEEDED_ARTIFACT_TYPES = Arrays.asList("dsn", "uman");
    final static List<SpecificationItemId> DEPEND_ON_IDS = parseIds("req~blah~4", "req~zoo~5");
    private static final String TITLE = "The title";

    private static List<SpecificationItemId> parseIds(final String... ids)
    {
        return Arrays.asList(ids).stream().map(SpecificationItemId::parseId)
                .collect(Collectors.toList());
    }

    // [utest.requirement_format~1]
    @Test
    void testBuildSimpleSpecificationItem()
    {
        final SpecificationItem.Builder builder = createSimpleItem();
        final SpecificationItem item = builder.build();
        assertSimpleItemComplete(item);
    }

    private SpecificationItem.Builder createSimpleItem()
    {
        final SpecificationItem.Builder builder = SpecificationItem.builder().id(ID);
        builder.title(TITLE).description(DESCRIPTION).rationale(RATIONALE).comment(COMMENT);
        return builder;
    }

    private void assertSimpleItemComplete(final SpecificationItem item)
    {
        assertThat(item.getId(), equalTo(ID));
        assertThat(item.getTitle(), equalTo(TITLE));
        assertThat(item.getDescription(), equalTo(DESCRIPTION));
        assertThat(item.getRationale(), equalTo(RATIONALE));
        assertThat(item.getComment(), equalTo(COMMENT));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testBuildComplexSpecificationItem()
    {
        final SpecificationItem.Builder builder = createSimpleItem();
        for (final SpecificationItemId coveredId : COVERED_IDS)
        {
            builder.addCoveredId(coveredId);
        }
        for (final SpecificationItemId dependOnId : DEPEND_ON_IDS)
        {
            builder.addDependOnId(dependOnId);
        }
        for (final String neededArtifactType : NEEDED_ARTIFACT_TYPES)
        {
            builder.addNeedsArtifactType(neededArtifactType);
        }
        final SpecificationItem item = builder.build();
        assertSimpleItemComplete(item);
        assertRelationsComplete(item);
    }

    private void assertRelationsComplete(final SpecificationItem item)
    {
        assertThat(item.getCoveredIds(), equalTo(COVERED_IDS));
        assertThat(item.getDependOnIds(), equalTo(DEPEND_ON_IDS));
        assertThat(item.getNeedsArtifactTypes(), equalTo(NEEDED_ARTIFACT_TYPES));
    }

    @Test
    void testBuildSpecificationItemFromSeparateIdParts()
    {
        final SpecificationItem.Builder builder = createTestItemBuilder();
        final SpecificationItem item = builder.build();
        assertThat(item.getId(), SpecificationItemIdMatcher.equalTo(ID));
    }

    @Test
    void testNeedsCoverageByArtifactType()
    {
        final SpecificationItem.Builder builder = createTestItemBuilder();
        builder.addNeedsArtifactType(NEEDED_ARTIFACT_TYPE);
        final SpecificationItem item = builder.build();
        assertThat(item.needsCoverageByArtifactType(NEEDED_ARTIFACT_TYPE), equalTo(true));
        assertThat(item.needsCoverageByArtifactType(NOT_NEEDED_ARTIFACT_TYPE), equalTo(false));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testNeedsCoverage()
    {
        final SpecificationItem.Builder builder = createTestItemBuilder();
        assertThat(builder.build().needsCoverage(), equalTo(false));
        builder.addNeedsArtifactType(NEEDED_ARTIFACT_TYPE);
        assertThat(builder.build().needsCoverage(), equalTo(true));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testLocationIsNullByDefault()
    {
        assertThat(createTestItemBuilder().build().getLocation(), equalTo(null));
    }

    @Test
    void testLocationBuilder()
    {
        final Location there = Location.create("there", 7);
        assertThat(createTestItemBuilder().location(there).build().getLocation(), equalTo(there));
    }

    @Test
    void testLocationBuilderFromComponents()
    {
        assertThat(createTestItemBuilder().location("here", 42).build().getLocation(),
                equalTo(Location.create("here", 42)));
    }

    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(SpecificationItem.class).verify();
    }

    @Test
    void testCoverageBuilderWithIdParts()
    {
        final SpecificationItem item = createTestItemBuilder() //
                .addCoveredId("foo", "bar", 3) //
                .build();
        assertThat(item.getCoveredIds(), equalTo(parseIds("foo~bar~3")));
    }

    private Builder createTestItemBuilder()
    {
        return SpecificationItem.builder() //
                .id(ARTIFACT_TYPE, NAME, REVISION);
    }

    @Test
    void testDependencyBuilderWithIdParts()
    {
        final SpecificationItem item = createTestItemBuilder().addDependOnId("foo", "bar", 3) //
                .build();
        assertThat(item.getDependOnIds(), equalTo(parseIds("foo~bar~3")));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testDefaultStatusIsApproved()
    {
        assertThat(createTestItemBuilder().build().getStatus(), equalTo(ItemStatus.APPROVED));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testBuildWithStatus()
    {
        assertThat(createTestItemBuilder().status(ItemStatus.REJECTED).build().getStatus(),
                equalTo(ItemStatus.REJECTED));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testByDefaultTagListIsEmpty()
    {
        assertThat(createTestItemBuilder().build().getTags(), emptyIterable());
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testTagBuilder()
    {
        assertThat(createTestItemBuilder().addTag("the_tag").build().getTags(),
                containsInAnyOrder("the_tag"));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testBuildingWithOutIdThrowsExepction()
    {
        final Builder builder = SpecificationItem.builder();
        assertThrows(IllegalStateException.class, () -> builder.build());
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testForwardsIsFalseByDefault()
    {
        assertThat(createSimpleItem().build().isForwarding(), equalTo(false));
    }

    // [utest->dsn~specification-item~3]
    @Test
    void testSetForwards()
    {
        assertThat(createSimpleItem().forwards(true).build().isForwarding(), equalTo(true));
    }

    @Test
    void testGetArtifactType()
    {
        assertThat(createSimpleItem().build().getArtifactType(), equalTo(ARTIFACT_TYPE));
    }

    @Test
    void testGetName()
    {
        assertThat(createSimpleItem().build().getName(), equalTo(NAME));
    }

    @Test
    void testGetRevision()
    {
        assertThat(createSimpleItem().build().getRevision(), equalTo(REVISION));
    }
}