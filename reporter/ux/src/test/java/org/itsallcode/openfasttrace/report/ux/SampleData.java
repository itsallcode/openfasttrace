package org.itsallcode.openfasttrace.report.ux;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;
import org.junit.jupiter.params.provider.Arguments;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SampleData {
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
    public static final List<LinkedSpecificationItem> LINKED_SAMPLE_ITEMS = new Linker(SAMPLE_ITEMS).link();
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
    public static final List<LinkedSpecificationItem> LINKED_SAMPLE_ITEMS_CYCLE = new Linker(SAMPLE_ITEM_CYCLE).link();
    public static final Map<String, Coverage> fromCoverages = Map.of(
            "fea", Coverage.COVERED,
            "arch", Coverage.UNCOVERED,
            "req", Coverage.NONE
    );
    public static final Map<String, Coverage> toCoverages = Map.of(
            "fea", Coverage.COVERED,
            "arch", Coverage.COVERED,
            "utest", Coverage.NONE
    );


    //
    // Helpers

    /**
     * Helper to produce tuples of all permutations of coverage types.
     */
    public static Stream<Arguments> provideCoveragePermutations() {
        return Arrays.stream(Coverage.values()).flatMap(firstCoverage ->
                Arrays.stream(Coverage.values()).map(secondCoverage ->
                        Arguments.of(firstCoverage, secondCoverage)
                ));
    }

    public static List<Matcher<? super Map<String, Coverage>>> coverages(Coverage... coverage) {
        final List<Coverage> stack = new ArrayList<>(Arrays.stream(coverage).toList());
        return ORDERED_SAMPLE_TYPES.stream().map(type ->
                Matchers.hasEntry(type, !stack.isEmpty() ? stack.remove(0) : Coverage.NONE)
        ).collect(Collectors.toList());
    }

    public static SpecificationItem item(final String id, ItemStatus status, Set<String> needs) {
        final SpecificationItemId specId = id(id);
        SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(specId)
                .title("Title " + id)
                .description("Descriptive text for " + id)
                .status(status);
        needs.forEach(builder::addNeedsArtifactType);

        return builder.build();
    }

    public static SpecificationItem item(final String id,
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

    public static SpecificationItemId id(final String id) {
        return id.matches("/~.*~") ?
                new SpecificationItemId.Builder(id).build() :
                new SpecificationItemId.Builder(id + "~1").build();
    }
}
