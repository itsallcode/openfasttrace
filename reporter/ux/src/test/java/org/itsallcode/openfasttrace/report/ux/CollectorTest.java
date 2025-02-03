package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.itsallcode.matcher.auto.AutoMatcher.containsInAnyOrder;

class CollectorTest {

    public static final List<String> ORDERED_SAMPLE_TYPES = List.of("fea", "req", "arch", "utest");

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

    private static final List<LinkedSpecificationItem> LINKED_SAMPLE_ITEMS = new Linker(SAMPLE_ITEMS).link();

    private Collector collector = null;

    @BeforeEach
    void setUp() {
        collector = new Collector().collect(LINKED_SAMPLE_ITEMS);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCollectAllTypes() {
        final Set<String> types = Collector.collectAllTypes(LINKED_SAMPLE_ITEMS);
        assertThat(types, containsInAnyOrder(ORDERED_SAMPLE_TYPES));
    }

    @Test
    void testCollectDependentTypes() {
        final Map<String, Collector.TypeDependencies> dependencies = Collector.collectDependentTypes(
                LINKED_SAMPLE_ITEMS);
        System.out.println(dependencies);
    }

    @Test
    void testCreateOrderedTypes() {
        final Set<String> allTypes = Collector.collectAllTypes(LINKED_SAMPLE_ITEMS);

        final List<LinkedSpecificationItem> items = LINKED_SAMPLE_ITEMS.stream().unordered().toList();
        final List<String> orderedTypes1 = Collector.createOrderedTypes(items, allTypes.stream().toList());
        System.out.println(String.join(",", orderedTypes1));
        assertThat(orderedTypes1, contains(ORDERED_SAMPLE_TYPES));

        final List<LinkedSpecificationItem> reverseItems = new ArrayList<>(LINKED_SAMPLE_ITEMS);
        Collections.reverse(reverseItems);
        final List<String> orderedTypes2 = Collector.createOrderedTypes(items, allTypes.stream().toList());
        System.out.println(String.join(",", orderedTypes2));
        assertThat(orderedTypes2, contains(ORDERED_SAMPLE_TYPES));
    }

    @Test
    void testItemCoverages() {
        final Map<LinkedSpecificationItem, Map<String, Coverage>> coverages = collector.getItemCoverages();
        System.out.println(coverages.entrySet().stream().map(entry ->
                String.format("%s%s", entry.getKey().getId(), entry.getValue())
        ).collect(Collectors.joining(",\n")));
    }

    @Test
    void testInitializedCoverages() {
        final Map<String,Coverage> coverages = Collector.initializedCoverages(ORDERED_SAMPLE_TYPES);
        System.out.println(coverages);
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