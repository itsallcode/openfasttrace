package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.itsallcode.openfasttrace.report.ux.model.UxSpecItem;
import org.itsallcode.openfasttrace.report.ux.model.WrongLinkType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.itsallcode.matcher.auto.AutoMatcher.containsInAnyOrder;
import static org.itsallcode.openfasttrace.report.ux.SampleData.LINKED_SAMPLE_WRONG_LINKS;
import static org.itsallcode.openfasttrace.report.ux.model.WrongLinkType.*;

class CollectorTest {

    @AfterEach
    void tearDown() {
    }

    // Collect SpecItems Types

    /**
     * Test extract SpecItems types as unordered set via Collector.collectAllTypes.
     */
    @Test
    void testCollectAllTypes() {
        final Set<String> types = Collector.collectAllTypes(SampleData.LINKED_SAMPLE_ITEMS);
        System.out.println("testCollectAllTypes " + types);
        assertThat(types, containsInAnyOrder(SampleData.ORDERED_SAMPLE_TYPES.toArray()));
    }

    @Test
    void testCollectDependentTypes() {
        final Map<String, Collector.TypeDependencies> dependencies = Collector.collectDependentTypes(
                SampleData.LINKED_SAMPLE_ITEMS);
        System.out.println(dependencies);
    }

    /**
     * Test creates an ordered list of SpecItems types with Collector.createOrderedTypes injecting SpecItems with a
     * cycle.
     */
    @Test
    void testCreateOrderedTypes() {
        // Order sample items with a cycle
        final List<String> orderedTypes1 = Collector.createOrderedTypes(SampleData.LINKED_SAMPLE_ITEMS_CYCLE);
        System.out.println(String.join(",", orderedTypes1));
        assertThat(orderedTypes1, contains(SampleData.ORDERED_SAMPLE_TYPES.toArray()));

        // Order sample items with a cycle in reverse order
        final List<LinkedSpecificationItem> reverseItems = new ArrayList<>(SampleData.LINKED_SAMPLE_ITEMS_CYCLE);
        Collections.reverse(reverseItems);
        final List<String> orderedTypes2 = Collector.createOrderedTypes(reverseItems);
        System.out.println(String.join(",", orderedTypes2));
        assertThat(orderedTypes2, contains(SampleData.ORDERED_SAMPLE_TYPES.toArray()));
    }

    // Collect coverages

    /**
     * Tests thatCollector.initializedCoverages return a Map with all SpecItem types set Coverage.NONE.
     */
    @Test
    void testInitializedCoverages() {
        final Map<String, Coverage> coverages = Collector.initializedCoverages(SampleData.ORDERED_SAMPLE_TYPES);
        System.out.println(coverages);
        assertThat(coverages, allOf(
                hasEntry("utest", Coverage.NONE),
                hasEntry("fea", Coverage.NONE),
                hasEntry("arch", Coverage.NONE),
                hasEntry("req", Coverage.NONE)
        ));
    }

    /**
     * Helper to produce tuples of all permutations of coverage types.
     */
    public static Stream<Arguments> provideCoveragePermutations() {
        return Arrays.stream(Coverage.values()).flatMap(firstCoverage ->
                Arrays.stream(Coverage.values()).map(secondCoverage ->
                        Arguments.of(firstCoverage, secondCoverage)
                ));
    }

    /**
     * Tests that Collector.mergeCoverType returns the fitting coverage for all permutations of coverage types.
     */
    @ParameterizedTest
    @MethodSource( "provideCoveragePermutations" )
    void testMergeCoverageType(final Coverage firstCoverage, final Coverage secondCoverage) {
        System.out.println(firstCoverage + ", " + secondCoverage);
        if( ( firstCoverage == Coverage.MISSING || secondCoverage == Coverage.MISSING ) ) {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.MISSING));
        }
        else if( ( firstCoverage == Coverage.UNCOVERED || secondCoverage == Coverage.UNCOVERED ) ) {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.UNCOVERED));
        }
        else if( firstCoverage == Coverage.COVERED || secondCoverage == Coverage.COVERED ) {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.COVERED));
        }
        else {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.NONE));
        }
    }

    /**
     * Test that  Collector.mergeCoverages with empty from return false and does not change to toCoverage.
     */
    @Test
    void testMergeCoverages() {
        final Map<String, Coverage> expectedToCoverages = Map.of(
                "fea", Coverage.COVERED,
                "arch", Coverage.UNCOVERED,
                "req", Coverage.NONE,
                "utest", Coverage.NONE
        );

        final Map<String, Coverage> toCoverageT1 = new HashMap<>(SampleData.toCoverages);
        final boolean result = Collector.mergeCoverages(SampleData.fromCoverages, toCoverageT1);
        assertThat(result, is(true));
        assertThat(toCoverageT1, equalTo(expectedToCoverages));
    }

    /**
     * Test that  Collector.mergeCoverages with empty from return false and does not change to toCoverage.
     */
    @Test
    void testMergeCoveragesWithEmptyFrom() {
        final Map<String, Coverage> toCoverageT1 = new HashMap<>(SampleData.toCoverages);
        assertThat(Collector.mergeCoverages(null, toCoverageT1), is(false));
        assertThat(toCoverageT1, equalTo(SampleData.toCoverages));
    }

    /**
     * Test updating (merging) an entry into Collector.itemCoverages with testUpdateItemCoverage.
     */
    @Test
    void testUpdateItemCoverageAddingFirstEntry() {
        final LinkedSpecificationItem sampleItem = new Linker(List.of(
                SampleData.item("req~req1", ItemStatus.APPROVED, Set.of("arch"))
        )).link().get(0);
        final Map<String, Coverage> sampleCoverages = new HashMap<>(Map.of(
                "fea", Coverage.NONE,
                "req", Coverage.NONE,
                "arch", Coverage.UNCOVERED,
                "utest", Coverage.COVERED
        ));

        final Collector collector = new Collector().collect(List.of());
        collector.itemCoverages.add(0, sampleCoverages);
        collector.updateItemCoverage(0, sampleItem.getArtifactType(), Coverage.COVERED, sampleCoverages);

        final List<Map<String, Coverage>> result = collector.getItemCoverages();
        assertThat(result.size(), is(1));
        assertThat(result.get(0), equalTo(Map.of(
                "fea", Coverage.NONE,
                "req", Coverage.COVERED,
                "arch", Coverage.UNCOVERED,
                "utest", Coverage.COVERED
        )));
    }

    /**
     * Test collected item coverage with {@link SampleData#SAMPLE_ITEMS}.
     */
    @Test
    void testItemCoverages() {
        final Coverage[][] expectedCoverages = {
                { Coverage.COVERED, Coverage.COVERED, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.COVERED, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.COVERED, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED },
        };

        final List<Map<String, Coverage>> coverages = new Collector()
                .collect(SampleData.LINKED_SAMPLE_ITEMS)
                .getItemCoverages();
        validateCoverages(SampleData.LINKED_SAMPLE_ITEMS, coverages, expectedCoverages);
    }

    /**
     * Test collected item coverage with {@link SampleData#LINKED_SAMPLE_ITEMS_CYCLE}.
     */
    @Test
    void testItemCoveragesWithCycle() {
        final Coverage[][] expectedCoverages = {
                { Coverage.COVERED, Coverage.COVERED, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.COVERED, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.COVERED, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.UNCOVERED, Coverage.MISSING },
                { Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED },
                { Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED }
        };

        final List<Map<String, Coverage>> coverages = new Collector()
                .collect(SampleData.LINKED_SAMPLE_ITEMS_CYCLE)
                .getItemCoverages();
        validateCoverages(SampleData.LINKED_SAMPLE_ITEMS_CYCLE, coverages, expectedCoverages);
    }

    @Test
    void testLinkTypes()
    {
        final Collector collector = new Collector().collect(LINKED_SAMPLE_WRONG_LINKS);
        assertThat(collector.getUxModel().getWrongLinkTypes(), containsInAnyOrder(WRONG_VERSION, UNWANTED, ORPHANED));
    }@Test
    void testCollectWrongLinkTypesWithWrongLinks()
    {
        final List<WrongLinkType> wrongLinkTypes = Collector.collectWrongLinkTypes(LINKED_SAMPLE_WRONG_LINKS);

        assertThat(wrongLinkTypes, hasSize(3));
        assertThat(wrongLinkTypes, containsInAnyOrder(WRONG_VERSION, ORPHANED, UNWANTED));
    }

    @Test
    void testCollectWrongLinkTypesWithNoWrongLinks()
    {
        final List<WrongLinkType> wrongLinkTypes = Collector.collectWrongLinkTypes(SampleData.LINKED_SAMPLE_ITEMS);

        assertThat(wrongLinkTypes, hasSize(0));
    }

    @Test
    void testCollectWrongLinkTypesEmptyItems()
    {
        final List<WrongLinkType> wrongLinkTypes = Collector.collectWrongLinkTypes(List.of());

        assertThat(wrongLinkTypes, hasSize(0));
    }

    @Test
    void testWrongLinks()
    {
        final Map<String, WrongLinkType> goldenWrongLinkTypeMapping = Map.of(
                "req~req_lower_version~1", WRONG_VERSION,
                "req~req_higher_version~1", WRONG_VERSION,
                "itest~itest_wrong_type~1", UNWANTED,
                "itest~itest_dead_type~1", ORPHANED
        );
        final Collector collector = new Collector().collect(LINKED_SAMPLE_WRONG_LINKS);
        final List<WrongLinkType> wrongLinkTypeMap = collector.getUxModel().getWrongLinkTypes();

        for (final UxSpecItem item : collector.getUxItems())
        {
            if ("feat".equals(item.getItem().getId().getArtifactType()))
                continue;

            final String id = item.getItem().getId().toString();
            final int expectedTypeIndex = wrongLinkTypeMap.indexOf(goldenWrongLinkTypeMapping.get(id));
            System.out.printf("Item links %s %s%n", item.getId(),
                    item.getWrongLinkTypes().stream()
                            .map(index -> wrongLinkTypeMap.get(index).getText())
                            .collect(Collectors.joining(","))
            );
            assertThat(item.getWrongLinkTypes(), hasItem(expectedTypeIndex));
        }
    }

    @Test
    void testWrongLinksByType()
    {
        final Map<String, List<String>> goldenWrongLinkTypeMapping = Map.of(
                "req:req_lower_version", List.of("feat:feat1:2[outdated]", "feat:feat1[orphaned]"),
                "req:req_higher_version", List.of("feat:feat1:2[predated]", "feat:feat1:3[orphaned]"),
                "itest:itest_wrong_type", List.of("feat:feat1:2[unwanted]"),
                "itest:itest_dead_type", List.of("feat:feat2:3[orphaned]")
        );

        final Collector collector = new Collector().collect(LINKED_SAMPLE_WRONG_LINKS);
        for (final UxSpecItem item : collector.getUxItems())
        {
            if ("feat".equals(item.getItem().getId().getArtifactType()))
                continue;

            final List<String> goldenSample = goldenWrongLinkTypeMapping.get(item.getId());
            assertThat(goldenSample,notNullValue());
            Map<String, String> targets = item.getWrongLinkTargets();
            for (Map.Entry<String, String> target : targets.entrySet())
            {
                final String targetValue = String.format("%s[%s]", target.getKey(), target.getValue());
                assertThat(targetValue,is(in(goldenSample)));
            }
        }
    }

    @Test
    void testCollectWrongLinkCountWithNoWrongLinks()
    {
        final List<WrongLinkType> wrongLinkTypes = List.of(WRONG_VERSION, ORPHANED, UNWANTED);
        final List<Integer> wrongLinkCount = Collector.collectWrongLinkCount(
                SampleData.LINKED_SAMPLE_ITEMS, wrongLinkTypes);

        assertThat(wrongLinkCount, hasSize(3));
        assertThat(wrongLinkCount, contains(0, 0, 0));
    }

    @Test
    void testCollectWrongLinkCountWithWrongLinks()
    {
        final List<WrongLinkType> wrongLinkTypes = Collector.collectWrongLinkTypes(LINKED_SAMPLE_WRONG_LINKS);
        final List<Integer> wrongLinkCount = Collector.collectWrongLinkCount(
                LINKED_SAMPLE_WRONG_LINKS, wrongLinkTypes);

        // wrongLinkTypes should be in the order: [WRONG_VERSION, ORPHANED, UNWANTED]
        // or could be in a different order, so we need to check based on the actual order
        final int versionIndex = wrongLinkTypes.indexOf(WRONG_VERSION);
        final int orphanedIndex = wrongLinkTypes.indexOf(ORPHANED);
        final int unwantedIndex = wrongLinkTypes.indexOf(UNWANTED);

        assertThat(wrongLinkCount, hasSize(3));
        // 2 version wrong links (1 outdated + 1 predated)
        assertThat(wrongLinkCount.get(versionIndex), is(2));
        // 3 orphaned links (1 from req_lower_version + 1 from req_higher_version + 1 from itest_dead_type)
        assertThat(wrongLinkCount.get(orphanedIndex), is(3));
        // 1 unwanted link (from itest_wrong_type)
        assertThat(wrongLinkCount.get(unwantedIndex), is(1));
    }

    @Test
    void testCollectWrongLinkCountEmptyItems()
    {
        final List<WrongLinkType> wrongLinkTypes = List.of(WRONG_VERSION, ORPHANED, UNWANTED);
        final List<Integer> wrongLinkCount = Collector.collectWrongLinkCount(
                List.of(), wrongLinkTypes);

        assertThat(wrongLinkCount, hasSize(3));
        assertThat(wrongLinkCount, contains(0, 0, 0));
    }

    @Test
    void testCollectWrongLinkCountEmptyWrongLinkTypes()
    {
        final List<Integer> wrongLinkCount = Collector.collectWrongLinkCount(
                LINKED_SAMPLE_WRONG_LINKS, List.of());

        assertThat(wrongLinkCount, hasSize(0));
    }

    @Test
    void testCollectWrongLinkCountIntegration()
    {
        final Collector collector = new Collector().collect(LINKED_SAMPLE_WRONG_LINKS);
        final List<Integer> wrongLinkCount = collector.getUxModel().getWrongLinkCount();
        final List<WrongLinkType> wrongLinkTypes = collector.getUxModel().getWrongLinkTypes();

        assertThat(wrongLinkCount, notNullValue());
        assertThat(wrongLinkCount, hasSize(wrongLinkTypes.size()));

        // Verify the total count matches expected
        final int totalWrongLinks = wrongLinkCount.stream().mapToInt(Integer::intValue).sum();
        assertThat(totalWrongLinks, is(6)); // 2 version + 3 orphaned + 1 unwanted = 6 total
    }

    // Collect Tag Count

    @Test
    void testCollectTagCountWithTags()
    {
        final Map<String, Integer> tagCount = Collector.collectTagCount(SampleData.LINKED_SAMPLE_ITEMS);

        assertThat(tagCount, notNullValue());
        assertThat(tagCount, hasEntry("v1", 1));
        assertThat(tagCount, hasEntry("v2", 1));
        assertThat(tagCount, hasEntry("v3", 1));
        assertThat(tagCount.size(), is(3));
    }

    @Test
    void testCollectTagCountNoTags()
    {
        final List<SpecificationItem> itemsWithoutTags = List.of(
                SampleData.item("req~req1", ItemStatus.APPROVED, Set.of("arch"))
        );
        final List<LinkedSpecificationItem> linkedItems = new Linker(itemsWithoutTags).link();
        final Map<String, Integer> tagCount = Collector.collectTagCount(linkedItems);

        assertThat(tagCount, notNullValue());
        assertThat(tagCount.isEmpty(), is(true));
    }

    @Test
    void testCollectTagCountMultipleItemsWithSameTags()
    {
        final List<SpecificationItem> itemsWithTags = List.of(
                SampleData.item("req~req1", ItemStatus.APPROVED, Set.of("arch"), Set.of(),
                        "content", List.of("tag1", "tag2")),
                SampleData.item("req~req2", ItemStatus.APPROVED, Set.of("arch"), Set.of(),
                        "content", List.of("tag1")),
                SampleData.item("req~req3", ItemStatus.APPROVED, Set.of("arch"), Set.of(),
                        "content", List.of("tag2", "tag3"))
        );
        final List<LinkedSpecificationItem> linkedItems = new Linker(itemsWithTags).link();
        final Map<String, Integer> tagCount = Collector.collectTagCount(linkedItems);

        assertThat(tagCount, hasEntry("tag1", 2));
        assertThat(tagCount, hasEntry("tag2", 2));
        assertThat(tagCount, hasEntry("tag3", 1));
        assertThat(tagCount.size(), is(3));
    }

    @Test
    void testCollectTagCountEmptyItems()
    {
        final Map<String, Integer> tagCount = Collector.collectTagCount(List.of());

        assertThat(tagCount, notNullValue());
        assertThat(tagCount.isEmpty(), is(true));
    }

    @Test
    void testCollectTagCountIntegration()
    {
        final Collector collector = new Collector().collect(SampleData.LINKED_SAMPLE_ITEMS);
        final List<String> tags = collector.getUxModel().getTags();
        final List<Integer> tagCounts = collector.getUxModel().getTagCount();

        assertThat(tags, hasSize(3));
        assertThat(tagCounts, hasSize(3));
        assertThat(tagCounts, everyItem(is(1))); // Each tag appears once in sample data
    }

    // Collect Type Count

    @Test
    void testCollectTypeCountWithTypes()
    {
        final List<String> orderedTypes = SampleData.ORDERED_SAMPLE_TYPES;
        final List<Integer> typeCount = Collector.collectTypeCount(
                SampleData.LINKED_SAMPLE_ITEMS, orderedTypes);

        assertThat(typeCount, hasSize(4));
        // fea: 1, req: 2, arch: 3, utest: 2
        assertThat(typeCount.get(orderedTypes.indexOf("fea")), is(1));
        assertThat(typeCount.get(orderedTypes.indexOf("req")), is(2));
        assertThat(typeCount.get(orderedTypes.indexOf("arch")), is(3));
        assertThat(typeCount.get(orderedTypes.indexOf("utest")), is(2));
    }

    @Test
    void testCollectTypeCountEmptyItems()
    {
        final List<String> orderedTypes = SampleData.ORDERED_SAMPLE_TYPES;
        final List<Integer> typeCount = Collector.collectTypeCount(List.of(), orderedTypes);

        assertThat(typeCount, hasSize(4));
        assertThat(typeCount, everyItem(is(0)));
    }

    @Test
    void testCollectTypeCountCountsForAllTypes()
    {
        final List<Integer> typeCount = Collector.collectTypeCount(
                SampleData.LINKED_SAMPLE_ITEMS, List.of("fea","req","arch","utest"));

        assertThat(typeCount, hasSize(4));
    }

    @Test
    void testCollectTypeCountIntegration()
    {
        final Collector collector = new Collector().collect(SampleData.LINKED_SAMPLE_ITEMS);
        final List<String> types = collector.getUxModel().getArtifactTypes();
        final List<Integer> typeCounts = collector.getUxModel().getTypeCount();

        assertThat(types, hasSize(4));
        assertThat(typeCounts, hasSize(4));

        final int totalItems = typeCounts.stream().mapToInt(Integer::intValue).sum();
        assertThat(totalItems, is(SampleData.LINKED_SAMPLE_ITEMS.size()));
    }

    // Collect Status Count

    @Test
    void testCollectStatusCountWithApprovedItems()
    {
        final List<Integer> statusCount = Collector.collectStatusCount(SampleData.LINKED_SAMPLE_ITEMS);

        assertThat(statusCount, hasSize(ItemStatus.values().length));
        // All sample items are APPROVED (index 0)
        assertThat(statusCount.get(ItemStatus.APPROVED.ordinal()), is(SampleData.LINKED_SAMPLE_ITEMS.size()));
        assertThat(statusCount.get(ItemStatus.PROPOSED.ordinal()), is(0));
        assertThat(statusCount.get(ItemStatus.DRAFT.ordinal()), is(0));
        assertThat(statusCount.get(ItemStatus.REJECTED.ordinal()), is(0));
    }

    @Test
    void testCollectStatusCountWithMixedStatuses()
    {
        final List<SpecificationItem> mixedStatusItems = List.of(
                SampleData.item("req~req1", ItemStatus.APPROVED, Set.of("arch")),
                SampleData.item("req~req2", ItemStatus.PROPOSED, Set.of("arch")),
                SampleData.item("req~req3", ItemStatus.DRAFT, Set.of("arch")),
                SampleData.item("req~req4", ItemStatus.REJECTED, Set.of("arch")),
                SampleData.item("req~req5", ItemStatus.APPROVED, Set.of("arch"))
        );
        final List<LinkedSpecificationItem> linkedItems = new Linker(mixedStatusItems).link();
        final List<Integer> statusCount = Collector.collectStatusCount(linkedItems);

        assertThat(statusCount, hasSize(ItemStatus.values().length));
        assertThat(statusCount.get(ItemStatus.APPROVED.ordinal()), is(2));
        assertThat(statusCount.get(ItemStatus.PROPOSED.ordinal()), is(1));
        assertThat(statusCount.get(ItemStatus.DRAFT.ordinal()), is(1));
        assertThat(statusCount.get(ItemStatus.REJECTED.ordinal()), is(1));
    }

    @Test
    void testCollectStatusCountEmptyItems()
    {
        final List<Integer> statusCount = Collector.collectStatusCount(List.of());

        assertThat(statusCount, hasSize(ItemStatus.values().length));
        assertThat(statusCount, everyItem(is(0)));
    }

    @Test
    void testCollectStatusCountIntegration()
    {
        final Collector collector = new Collector().collect(SampleData.LINKED_SAMPLE_ITEMS);
        final List<String> statusNames = collector.getUxModel().getStatusNames();
        final List<Integer> statusCounts = collector.getUxModel().getStatusCount();

        assertThat(statusNames, hasSize(ItemStatus.values().length));
        assertThat(statusCounts, hasSize(ItemStatus.values().length));

        final int totalItems = statusCounts.stream().mapToInt(Integer::intValue).sum();
        assertThat(totalItems, is(SampleData.LINKED_SAMPLE_ITEMS.size()));
    }

    //
    // Helper


    private void validateCoverages(final List<LinkedSpecificationItem> specItems,
                                   final List<Map<String, Coverage>> returnedCoverages,
                                   final Coverage[][] expectedCoverages) {
        for( int index = 0; index < expectedCoverages.length; index++ ) {
            validateCoverage(index, specItems, returnedCoverages, expectedCoverages[index]);
        }
    }

    private void validateCoverage(int index,
                                  List<LinkedSpecificationItem> specItems,
                                  List<Map<String, Coverage>> returnedCoverages,
                                  Coverage... expectedCoverages) {
        final LinkedSpecificationItem specItem = specItems.get(index);
        final Map<String, Coverage> returnedCoverage = returnedCoverages.get(index);
        System.out.printf("%d: %s%s matching {%s}\n", index,
                specItem.getArtifactType(),
                returnedCoverage,
                Arrays.stream(expectedCoverages).map(Coverage::toString).collect(Collectors.joining(",")));
        assertThat(returnedCoverage, allOf(SampleData.coverages(expectedCoverages)));
    }

} // CollectorTest