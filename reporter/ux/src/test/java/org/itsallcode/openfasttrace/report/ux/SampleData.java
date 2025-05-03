package org.itsallcode.openfasttrace.report.ux;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.api.core.ItemStatus;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.core.Linker;
import org.itsallcode.openfasttrace.report.ux.model.Coverage;

import java.util.*;
import java.util.stream.Collectors;

public class SampleData {

    public static final String LONG_SAMPLE_CONTENT =
            "Officia voluptate aliquip ullamco dolore irure sint occaecat dolore eu proident."
                    + " Lorem cupidatat dolore voluptate non nulla commodo sint. Aliquip velit anim tem"
                    + "por magna culpa in esse. Excepteur anim ea ex est anim minim esse ut. Deserunt e"
                    + "nim veniam amet quis veniam amet in velit esse. Pariatur ut aliquip ipsum dolore"
                    + " quis reprehenderit excepteur adipisicing.Reprehenderit laboris reprehenderit re"
                    + "prehenderit irure aute eiusmod fugiat dolore ipsum velit mollit cillum. Commodo "
                    + "minim dolore nisi nostrud enim nisi reprehenderit aliqua anim deserunt ea ut eli"
                    + "t. Aute Lorem quis elit proident veniam sunt duis aliquip. Duis duis ad nostrud "
                    + "adipisicing. Consequat laboris qui aute cillum do eu non. Tempor commodo adipisi"
                    + "cing eu exercitation laboris.";

    public static final List<String> SAMPLE_TAGS = List.of( "v1", "v2", "v3" );

    /**
     * Coverage types in ordered from based on SAMPLE_ITEM linkage
     */
    public static final List<String> ORDERED_SAMPLE_TYPES = List.of("fea", "req", "arch", "utest");

    /**
     * Generated samples data with project name removed.
     */
    public static final String SAMPLE_OUTPUT_RESOURCE = "sample_jsgenerator_result.js";

    /**
     * Sample for items on all level fea,req,arch,utest with upwards linkes
     */
    public static final List<SpecificationItem> SAMPLE_ITEMS = List.of(
            item("fea~fea1", ItemStatus.APPROVED, Set.of("req")),
            item("req~req1", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("req~req2", ItemStatus.APPROVED, Set.of("arch"), Set.of("fea~fea1")),
            item("arch~arch1", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch2", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req1")),
            item("arch~arch3", ItemStatus.APPROVED, Set.of("utest"), Set.of("req~req2"),LONG_SAMPLE_CONTENT, SAMPLE_TAGS),
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

    public static List<Matcher<? super Map<String, Coverage>>> coverages(final Coverage... coverage) {
        final List<Coverage> stack = new ArrayList<>(Arrays.stream(coverage).toList());
        return ORDERED_SAMPLE_TYPES.stream().map(type ->
                Matchers.hasEntry(type, !stack.isEmpty() ? stack.remove(0) : Coverage.NONE)
        ).collect(Collectors.toList());
    }

    public static SpecificationItem item(final String id, final ItemStatus status, final Set<String> needs) {
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
                                         final ItemStatus status,
                                         final Set<String> needs,
                                         final Set<String> coverages) {
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

    public static SpecificationItem item(final String id,
            final ItemStatus status,
            final Set<String> needs,
            final Set<String> coverages,
            final String content,
            final List<String> tags) {
        final SpecificationItemId specId = id(id);
        SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(specId)
                .title("Title " + id)
                .description(content)
                .status(status);
        needs.forEach(builder::addNeedsArtifactType);
        tags.forEach(builder::addTag);
        coverages.forEach(coverage -> builder.addCoveredId(id(coverage)));

        return builder.build();
    }

    public static SpecificationItemId id(final String id) {
        return id.matches("/~.*~") ?
                new SpecificationItemId.Builder(id).build() :
                new SpecificationItemId.Builder(id + "~1").build();
    }

} // SampleData
