package org.itsallcode.openfasttrace.api.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import static org.hamcrest.Matchers.equalTo;

import java.util.*;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.junit.jupiter.api.Test;

class TestSpecificationListBuilder
{

    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    @Test
    void testBuildBasicItem()
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
    void testBuildWithStatus()
    {
        final SpecificationListBuilder builder = createBasicListBuilder();
        builder.setStatus(ItemStatus.DRAFT);
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getStatus(), equalTo(ItemStatus.DRAFT));
    }

    @Test
    void testBuildWithTags()
    {
        final SpecificationListBuilder builder = createBasicListBuilder();
        builder.addTag("foo");
        builder.addTag("bar");
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getTags(), containsInAnyOrder("foo", "bar"));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    void testFilterArtifactOfType()
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
    void testFilterNeededArtifactType()
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
    void testFilterCoversLinkWithArtifactType()
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
    void testFilterDependsLinkWithArtifactType()
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
    void testDuplicateIdNotIgnored()
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
    void testFilterSpecificationItemsByTags()
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
        assertThat(items.stream().map(SpecificationItem::getName).collect(Collectors.toList()),
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
    void testFilterSpecificationItemsByTagsIncludingNoTags()
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
        assertThat(items.stream().map(SpecificationItem::getName).collect(Collectors.toList()),
                containsInAnyOrder("in-A", "in-B", "in-D"));
    }
}
