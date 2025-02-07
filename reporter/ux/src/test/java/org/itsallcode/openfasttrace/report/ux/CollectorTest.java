package org.itsallcode.openfasttrace.report.ux;

import org.hamcrest.Matcher;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

class CollectorTest {

    /**
     * Coverage types in ordered from based on SAMPLE_ITEM linkage
     */
    public static final List<String> ORDERED_SAMPLE_TYPES = List.of("fea", "req", "arch", "utest");

    /**
     * Sample for items on all level fea,req,arch,utest with upwards linkes
     */
    public static final List<SpecificationItem> SAMPLE_ITEMS = List.of(
            item("fea~fea1", ItemStatus.APPROVED, Set.of("req")),
            item("req~req1", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("req~req2", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("arch~arch1", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch2", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch3", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req2")),
            item("utest~utest1", ItemStatus.APPROVED, Set.of(), Set.of("arch~arch1")),
            item("utest~utest2", ItemStatus.APPROVED, Set.of(), Set.of("arch~arch2"))
    );

    /**
     * Sample for items on all level fea,req,arch,utest with a circular link
     */
    public static final List<SpecificationItem> SAMPLE_ITEM_CYCLE = List.of(
            item("fea~fea1", ItemStatus.APPROVED, Set.of("req")),
            item("req~req1", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("req~req2", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("arch~arch1", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch2", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch3", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req2")),
            item("arch~cycle", ItemStatus.APPROVED, Set.of("utest"), Set.of("utest~cycle")),
            item("utest~utest1", ItemStatus.APPROVED, Set.of(), Set.of("arch~arch1")),
            item("utest~utest2", ItemStatus.APPROVED, Set.of(), Set.of("arch~arch2")),
            item("utest~cycle", ItemStatus.APPROVED, Set.of(), Set.of("arch~cycle"))
    );

    private static final List<LinkedSpecificationItem> LINKED_SAMPLE_ITEMS = new Linker(SAMPLE_ITEMS).link();
    private static final List<LinkedSpecificationItem> LINKED_SAMPLE_ITEMS_CYCLE = new Linker(SAMPLE_ITEM_CYCLE).link();

    private Collector collector = null;

    @BeforeEach
    void setUp() {
        collector = new Collector().collect(LINKED_SAMPLE_ITEMS);
    }

    @AfterEach
    void tearDown() {
    }

    // Collect SpecItems Types

    /**
     * Test extract SpecItems types as unordered set via Collector.collectAllTypes.
     */
    @Test
    void testCollectAllTypes() {
        final Set<String> types = Collector.collectAllTypes(LINKED_SAMPLE_ITEMS);
        System.out.println("testCollectAllTypes " + types);
        assertThat(types, containsInAnyOrder(ORDERED_SAMPLE_TYPES.toArray()));
    }

    @Test
    void testCollectDependentTypes() {
        final Map<String, Collector.TypeDependencies> dependencies = Collector.collectDependentTypes(
                LINKED_SAMPLE_ITEMS);
        System.out.println(dependencies);
    }

    /**
     * Test creates an ordered list of SpecItems types with Collector.createOrderedTypes injecting SpecItems with a
     * cycle.
     */
    @Test
    void testCreateOrderedTypes() {
        // Order sample items with a cycle
        final List<String> orderedTypes1 = Collector.createOrderedTypes(LINKED_SAMPLE_ITEMS_CYCLE);
        System.out.println(String.join(",", orderedTypes1));
        assertThat(orderedTypes1, contains(ORDERED_SAMPLE_TYPES.toArray()));

        // Order sample items with a cycle in reverse order
        final List<LinkedSpecificationItem> reverseItems = new ArrayList<>(LINKED_SAMPLE_ITEMS_CYCLE);
        Collections.reverse(reverseItems);
        final List<String> orderedTypes2 = Collector.createOrderedTypes(reverseItems);
        System.out.println(String.join(",", orderedTypes2));
        assertThat(orderedTypes2, contains(ORDERED_SAMPLE_TYPES.toArray()));
    }

    // Collect coverages

    /**
     * Tests thatCollector.initializedCoverages return a Map with all SpecItem types set Coverage.NONE.
     */
    @Test
    void testInitializedCoverages() {
        final Map<String, Coverage> coverages = Collector.initializedCoverages(ORDERED_SAMPLE_TYPES);
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
    private static Stream<Arguments> provideCoveragePermutations() {
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
        if( ( firstCoverage == Coverage.UNCOVERED || secondCoverage == Coverage.UNCOVERED ) ) {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.UNCOVERED));
        }
        else if( firstCoverage == Coverage.COVERED || secondCoverage == Coverage.COVERED ) {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.COVERED));
        }
        else {
            assertThat(Collector.mergeCoverType(firstCoverage, secondCoverage), equalTo(Coverage.NONE));
        }
    }

    private static final Map<String, Coverage> fromCoverages = Map.of(
            "fea", Coverage.COVERED,
            "arch", Coverage.UNCOVERED,
            "req", Coverage.NONE
    );
    private static final Map<String, Coverage> toCoverages = Map.of(
            "fea", Coverage.COVERED,
            "arch", Coverage.COVERED,
            "utest", Coverage.NONE
    );

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

        final Map<String, Coverage> toCoverageT1 = new HashMap<>(toCoverages);
        final boolean result = Collector.mergeCoverages(fromCoverages, toCoverageT1);
        assertThat(result, is(true));
        assertThat(toCoverageT1, equalTo(expectedToCoverages));
    }

    /**
     * Test that  Collector.mergeCoverages with empty from return false and does not change to toCoverage.
     */
    @Test
    void testMergeCoveragesWithEmptyFrom() {
        final Map<String, Coverage> toCoverageT1 = new HashMap<>(toCoverages);
        assertThat(Collector.mergeCoverages(null, toCoverageT1), is(false));
        assertThat(toCoverageT1, equalTo(toCoverages));
    }

    /**
     * Test updating (merging) an entry into Collector.itemCoverages with testUpdateItemCoverage.
     */
    @Test
    void testUpdateItemCoverageAddingFirstEntry() {
        final LinkedSpecificationItem sampleItem = new Linker(List.of(
                item("req~req1", ItemStatus.APPROVED, Set.of("arch"))
        )).link().get(0);
        final Map<String, Coverage> sampleCoverages = new HashMap<>(Map.of(
                "fea", Coverage.NONE,
                "req", Coverage.NONE,
                "arch", Coverage.UNCOVERED,
                "utest", Coverage.COVERED
        ));

        final Collector collector = new Collector().collect(List.of());
        collector.itemCoverages.add(0, sampleCoverages);
        collector.updateItemCoverage(0, sampleItem.getArtifactType(), true, sampleCoverages);

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
     * Test collected item coverage with {@link #SAMPLE_ITEMS}.
     */
    @Test
    void testItemCoverages() {
        final List<Map<String, Coverage>> coverages = collector.getItemCoverages();
        System.out.println("0:" + coverages.get(0));
        assertThat(coverages.get(0),
                allOf(coverages(Coverage.COVERED, Coverage.COVERED, Coverage.UNCOVERED, Coverage.COVERED)));
        System.out.println("1:" + coverages.get(1));
        assertThat(coverages.get(1),
                allOf(coverages(Coverage.NONE, Coverage.COVERED, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("2:" + coverages.get(2));
        assertThat(coverages.get(2),
                allOf(coverages(Coverage.NONE, Coverage.COVERED, Coverage.UNCOVERED, Coverage.NONE)));
        System.out.println("3:" + coverages.get(3));
        assertThat(coverages.get(3),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("4:" + coverages.get(4));
        assertThat(coverages.get(4),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("5:" + coverages.get(5));
        assertThat(coverages.get(5),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.UNCOVERED)));
        System.out.println("6:" + coverages.get(6));
        assertThat(coverages.get(6),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.NONE, Coverage.COVERED)));
        System.out.println("7:" + coverages.get(7));
        assertThat(coverages.get(7),
                allOf(coverages(Coverage.NONE,Coverage.NONE, Coverage.NONE, Coverage.COVERED)));
    }

    private static List<Matcher<? super Map<String, Coverage>>> coverages(Coverage... coverage) {
        final List<Coverage> stack = new ArrayList<>(Arrays.stream(coverage).toList());
        return ORDERED_SAMPLE_TYPES.stream().map(type ->
                hasEntry(type, !stack.isEmpty() ? stack.remove(0) : Coverage.NONE)
        ).collect(Collectors.toList());
    }

    /**
     * Test collected item coverage with {@link #LINKED_SAMPLE_ITEMS_CYCLE}.
     */
    @Test
    void testItemCoveragesWithCycle() {
        final List<Map<String, Coverage>> coverages = collector.collect(LINKED_SAMPLE_ITEMS_CYCLE).getItemCoverages();
        System.out.println(coverages.stream().map(Object::toString).collect(Collectors.joining(",\n")));
        System.out.println("0:" + coverages.get(0));
        assertThat(coverages.get(0),
                allOf(coverages(Coverage.COVERED, Coverage.COVERED, Coverage.UNCOVERED, Coverage.COVERED)));
        System.out.println("1:" + coverages.get(1));
        assertThat(coverages.get(1),
                allOf(coverages(Coverage.NONE, Coverage.COVERED, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("2:" + coverages.get(2));
        assertThat(coverages.get(2),
                allOf(coverages(Coverage.NONE, Coverage.COVERED, Coverage.UNCOVERED, Coverage.NONE)));
        System.out.println("3:" + coverages.get(3));
        assertThat(coverages.get(3),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("4:" + coverages.get(4));
        assertThat(coverages.get(4),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("5:" + coverages.get(5));
        assertThat(coverages.get(5),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.UNCOVERED)));
        System.out.println("6:" + coverages.get(6));
        assertThat(coverages.get(6),
                allOf(coverages(Coverage.NONE, Coverage.NONE, Coverage.COVERED, Coverage.COVERED)));
        System.out.println("7:" + coverages.get(7));
        assertThat(coverages.get(7),
                allOf(coverages(Coverage.NONE,Coverage.NONE, Coverage.NONE, Coverage.COVERED)));
        System.out.println("8:" + coverages.get(8));
        assertThat(coverages.get(8),
                allOf(coverages(Coverage.NONE,Coverage.NONE, Coverage.NONE, Coverage.COVERED)));
        System.out.println("9:" + coverages.get(8));
        assertThat(coverages.get(9),
                allOf(coverages(Coverage.NONE,Coverage.NONE, Coverage.NONE, Coverage.COVERED)));
    }

    //
    // Helpers

    private static SpecificationItem item(final String id, ItemStatus status, Set<String> needs) {
        final SpecificationItemId specId = id(id);
        SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(specId)
                .title("Title " + id)
                .description("Descriptive text for " + id)
                .status(status);
        needs.forEach(builder::addNeedsArtifactType);

        return builder.build();
    }

    private static SpecificationItem item(final String id,
                                          ItemStatus status,
                                          Set<String> needs,
                                          Set<String> coverages) {
        final SpecificationItemId specId = id(id);
        SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(specId)
                .title("Title " + id)
                .description("Descriptive text for " + id)
                .status(status);
        needs.forEach(builder::addNeedsArtifactType);
        coverages.forEach(coverage -> builder.addCoveredId(id(coverage)));

        return builder.build();
    }

    private static SpecificationItemId id(final String id) {
        return id.matches("/~.*~") ?
                new SpecificationItemId.Builder(id).build() :
                new SpecificationItemId.Builder(id + "~1").build();
    }
}