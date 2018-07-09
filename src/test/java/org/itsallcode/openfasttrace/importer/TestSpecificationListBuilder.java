package org.itsallcode.openfasttrace.importer;

import static org.hamcrest.Matchers.containsInAnyOrder;

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
import static org.junit.Assert.assertThat;

import java.util.*;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.core.ItemStatus;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.junit.Test;;

public class TestSpecificationListBuilder
{

    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    @Test
    public void testBuildBasicItem()
    {
        final SpecificationListBuilder builder = createBasicListBuilder();
        builder.appendDescription(DESCRIPTION);
        builder.setTitle(TITLE);
        final List<SpecificationItem> items = builder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(ID));
        assertThat(items.get(0).getDescription(), equalTo(DESCRIPTION));
        assertThat(items.get(0).getTitle(), equalTo(TITLE));
    }

    private SpecificationListBuilder createBasicListBuilder()
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.beginSpecificationItem();
        builder.setId(ID);
        return builder;
    }

    @Test
    public void testBuildWithStatus()
    {
        final SpecificationListBuilder builder = createBasicListBuilder();
        builder.setStatus(ItemStatus.DRAFT);
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getStatus(), equalTo(ItemStatus.DRAFT));
    }

    @Test
    public void testBuildWithTags()
    {
        final SpecificationListBuilder builder = createBasicListBuilder();
        builder.addTag("foo");
        builder.addTag("bar");
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getTags(), containsInAnyOrder("foo", "bar"));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterArtifactOfType()
    {
        final SpecificationListBuilder builder = createListBuilderFilteringByArtifactTypes("dsn");
        builder.beginSpecificationItem();
        builder.setId(SpecificationItemId.createId("impl", "ignore", 1));
        builder.endSpecificationItem();
        builder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        builder.setId(importedId);
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(importedId));
    }

    private SpecificationListBuilder createListBuilderFilteringByArtifactTypes(
            final String... artifactTypes)
    {
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .artifactTypes(new HashSet<>(Arrays.asList(artifactTypes))) //
                .build();
        return SpecificationListBuilder.createWithFilter(filterSettings);
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterNeededArtifactType()
    {
        final SpecificationListBuilder builder = createListBuilderFilteringByArtifactTypes("dsn",
                "utest", "itest");
        builder.beginSpecificationItem();
        final SpecificationItemId id = SpecificationItemId.createId("dsn", "import", 1);
        builder.setId(id);
        builder.addNeededArtifactType("impl");
        builder.addNeededArtifactType("utest");
        builder.addNeededArtifactType("itest");
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getNeedsArtifactTypes(), containsInAnyOrder("utest", "itest"));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterCoversLinkWithArtifactType()
    {
        final SpecificationItemId acceptedId = SpecificationItemId.createId("utest", "accept", 2);
        final SpecificationItemId rejectedId = SpecificationItemId.createId("impl", "reject", 3);
        final SpecificationListBuilder builder = createListBuilderFilteringByArtifactTypes("utest",
                "dsn");
        builder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        builder.setId(importedId);
        builder.addCoveredId(acceptedId);
        builder.addCoveredId(rejectedId);
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getCoveredIds(), containsInAnyOrder(acceptedId));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterDependsLinkWithArtifactType()
    {
        final SpecificationItemId acceptedId = SpecificationItemId.createId("utest", "accept", 2);
        final SpecificationItemId rejectedId = SpecificationItemId.createId("impl", "reject", 3);
        final SpecificationListBuilder builder = createListBuilderFilteringByArtifactTypes("utest",
                "dsn");
        builder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        builder.setId(importedId);
        builder.addDependsOnId(acceptedId);
        builder.addDependsOnId(rejectedId);
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getDependOnIds(), containsInAnyOrder(acceptedId));
    }

    @Test
    public void testDuplicateIdNotIgnored()
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.beginSpecificationItem();
        builder.setId(ID);
        builder.endSpecificationItem();
        builder.beginSpecificationItem();
        builder.setId(ID);
        builder.endSpecificationItem();
        assertThat(builder.getItemCount(), equalTo(2));
    }

    // [utest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testFilterSpecificationItemsByTags()
    {
        final Set<String> wantedTags = new HashSet<>();
        wantedTags.add("client");
        wantedTags.add("server");
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .tags(wantedTags) //
                .withoutTags(false) //
                .build();
        final SpecificationListBuilder builder = SpecificationListBuilder
                .createWithFilter(filterSettings);
        addItemWithTags(builder, "in-A", "client", "database");
        addItemWithTags(builder, "in-B", "server", "database");
        addItemWithTags(builder, "out-C", "exporter", "database");
        addItemWithTags(builder, "out-D");
        final List<SpecificationItem> items = builder.build();
        assertThat(items.stream().map(item -> item.getId().getName()).collect(Collectors.toList()),
                containsInAnyOrder("in-A", "in-B"));
    }

    private void addItemWithTags(final SpecificationListBuilder builder, final String name,
            final String... tags)
    {
        builder.beginSpecificationItem();
        final SpecificationItemId idA = SpecificationItemId.createId("dsn", name, 1);
        builder.setId(idA);
        for (final String tag : tags)
        {
            builder.addTag(tag);
        }
        builder.endSpecificationItem();
    }

    // [utest->dsn~filtering-by-tags-or-no-tags-during-import~1]
    @Test
    public void testFilterSpecificationItemsByTagsIncludingNoTags()
    {
        final Set<String> wantedTags = new HashSet<>();
        wantedTags.add("client");
        wantedTags.add("server");
        final FilterSettings filterSettings = new FilterSettings.Builder() //
                .tags(wantedTags) //
                .build();
        final SpecificationListBuilder builder = SpecificationListBuilder
                .createWithFilter(filterSettings);
        addItemWithTags(builder, "in-A", "client", "database");
        addItemWithTags(builder, "in-B", "server", "database");
        addItemWithTags(builder, "out-C", "exporter", "database");
        addItemWithTags(builder, "in-D");
        final List<SpecificationItem> items = builder.build();
        assertThat(items.stream().map(item -> item.getId().getName()).collect(Collectors.toList()),
                containsInAnyOrder("in-A", "in-B", "in-D"));
    }
}
