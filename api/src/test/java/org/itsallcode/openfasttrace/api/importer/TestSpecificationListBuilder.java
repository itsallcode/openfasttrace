package org.itsallcode.openfasttrace.api.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.*;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.junit.jupiter.api.Test;

class TestSpecificationListBuilder
{
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    // [utest->dsn~located-specification-item-id-storage~1]
    @Test
    void testPreservesLocatedIdOccurrences()
    {
        final SpecificationItemId coveredId = SpecificationItemId.parseId("req~covered~1");
        final SpecificationItemId dependencyId = SpecificationItemId.parseId("req~dependency~1");
        final LocatedSpecificationItemId locatedId = locatedId(ID, 0);
        final LocatedSpecificationItemId firstCoveredId = locatedId(coveredId, 5);
        final LocatedSpecificationItemId secondCoveredId = locatedId(coveredId, 20);
        final LocatedSpecificationItemId locatedDependencyId = locatedId(dependencyId, 3);
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.beginSpecificationItem();
        builder.setId(locatedId);
        builder.addCoveredId(firstCoveredId);
        builder.addCoveredId(secondCoveredId);
        builder.addDependsOnId(locatedDependencyId);

        final SpecificationItem item = builder.build().get(0);

        assertAll(
                () -> assertThat(item.getLocatedId(), equalTo(locatedId)),
                () -> assertThat(item.getLocatedCoveredIds(), contains(firstCoveredId, secondCoveredId)),
                () -> assertThat(item.getLocatedDependOnIds(), contains(locatedDependencyId)),
                () -> assertThat(item.getCoveredIds(), contains(coveredId, coveredId)),
                () -> assertThat(item.getDependOnIds(), contains(dependencyId)));
    }

    private static LocatedSpecificationItemId locatedId(final String artifactType, final String name,
            final int revision)
    {
        return locatedId(SpecificationItemId.createId(artifactType, name, revision));
    }

    private static LocatedSpecificationItemId locatedId(final SpecificationItemId id)
    {
        return LocatedSpecificationItemId.builder().id(id).build();
    }

    private static LocatedSpecificationItemId locatedId(final SpecificationItemId id, final int column)
    {
        final SourceRange range = new SourceRange(new SourcePosition(0, column),
                new SourcePosition(0, column + id.toString().length()));
        return LocatedSpecificationItemId.builder().id(id).range(range).build();
    }

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
        builder.setId(locatedId(ID));
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

    @Test
    void testAddSpecificationItem()
    {
        final SpecificationItem item = SpecificationItem.builder().id(ID).build();
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.addSpecificationItem(item);
        assertThat(builder.build(), contains(item));
    }

    // [utest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    void testFilterArtifactOfType()
    {
        final SpecificationListBuilder builder = createListBuilderFilteringByArtifactTypes("dsn");
        builder.beginSpecificationItem();
        builder.setId(locatedId("impl", "ignore", 1));
        builder.endSpecificationItem();
        builder.beginSpecificationItem();
        final SpecificationItemId importedId = SpecificationItemId.createId("dsn", "import", 1);
        builder.setId(locatedId(importedId));
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(importedId));
    }

    private SpecificationListBuilder createListBuilderFilteringByArtifactTypes(
            final String... artifactTypes)
    {
        final FilterSettings filterSettings = FilterSettings.builder() //
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
        builder.setId(locatedId(id));
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
        builder.setId(locatedId(importedId));
        builder.addCoveredId(locatedId(acceptedId));
        builder.addCoveredId(locatedId(rejectedId));
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
        builder.setId(locatedId(importedId));
        builder.addDependsOnId(locatedId(acceptedId));
        builder.addDependsOnId(locatedId(rejectedId));
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        assertThat(items.get(0).getDependOnIds(), containsInAnyOrder(acceptedId));
    }

    @Test
    void testDuplicateIdNotIgnored()
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.beginSpecificationItem();
        builder.setId(locatedId(ID));
        builder.endSpecificationItem();
        builder.beginSpecificationItem();
        builder.setId(locatedId(ID));
        builder.endSpecificationItem();
        assertThat(builder.getItemCount(), equalTo(2));
    }

    // [utest->dsn~filtering-by-item-status-during-import~1]
    @Test
    void testFilterSpecificationItemsByStatus()
    {
        final FilterSettings filterSettings = FilterSettings.builder()
                .wantedStatuses(Set.of(ItemStatus.DRAFT))
                .build();
        final SpecificationListBuilder builder = SpecificationListBuilder
                .createWithFilter(filterSettings);
        addItemWithStatus(builder, "in-A", ItemStatus.DRAFT);
        addItemWithStatus(builder, "out-B", ItemStatus.APPROVED);
        addItemWithStatus(builder, "out-C", ItemStatus.PROPOSED);
        addItemWithStatus(builder, "out-D", ItemStatus.REJECTED);
        // out-E becomes APPROVED by default
        addItemWithStatus(builder, "out-E", null);
        final List<SpecificationItem> items = builder.build();
        assertThat(items.stream().map(SpecificationItem::getName).toList(),
                containsInAnyOrder("in-A"));
    }

    private void addItemWithStatus(final SpecificationListBuilder builder, final String name,
            final ItemStatus status)
    {
        builder.beginSpecificationItem();
        final SpecificationItemId id = SpecificationItemId.createId("dsn", name, 1);
        builder.setId(locatedId(id));
        if (status != null)
        {
            builder.setStatus(status);
        }
        builder.endSpecificationItem();
    }

    // [utest->dsn~filtering-by-tags-during-import~1]
    @Test
    void testFilterSpecificationItemsByTags()
    {
        final Set<String> wantedTags = new HashSet<>();
        wantedTags.add("client");
        wantedTags.add("server");
        final FilterSettings filterSettings = FilterSettings.builder() //
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
        assertThat(items.stream().map(SpecificationItem::getName).toList(),
                containsInAnyOrder("in-A", "in-B"));
    }

    private void addItemWithTags(final SpecificationListBuilder builder, final String name,
            final String... tags)
    {
        builder.beginSpecificationItem();
        final SpecificationItemId idA = SpecificationItemId.createId("dsn", name, 1);
        builder.setId(locatedId(idA));
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
        final FilterSettings filterSettings = FilterSettings.builder() //
                .tags(wantedTags) //
                .build();
        final SpecificationListBuilder builder = SpecificationListBuilder
                .createWithFilter(filterSettings);
        addItemWithTags(builder, "in-A", "client", "database");
        addItemWithTags(builder, "in-B", "server", "database");
        addItemWithTags(builder, "out-C", "exporter", "database");
        addItemWithTags(builder, "in-D");
        final List<SpecificationItem> items = builder.build();
        assertThat(items.stream().map(SpecificationItem::getName).toList(),
                containsInAnyOrder("in-A", "in-B", "in-D"));
    }

    // [utest->dsn~cleaning-imported-multi-line-text-elements~1]
    @Test
    void testMultilineTextFieldsGetTrimmed()
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        builder.beginSpecificationItem();
        builder.setId(locatedId("foo", "bar", 1));
        builder.appendComment(" a comment ");
        builder.appendDescription("   a description\t \t");
        builder.appendRationale("\n\na   rationale\n  \n");
        builder.endSpecificationItem();
        final List<SpecificationItem> items = builder.build();
        final SpecificationItem item = items.get(0);
        assertAll(
                () -> assertThat(item.getComment(), equalTo("a comment")),
                () -> assertThat(item.getDescription(), equalTo("a description")),
                () -> assertThat(item.getRationale(), equalTo("a   rationale")));
    }
}
