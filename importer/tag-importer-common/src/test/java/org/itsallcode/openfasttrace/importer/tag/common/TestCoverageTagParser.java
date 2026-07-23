package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

class TestCoverageTagParser {
    private static final String FILE = "source.file";

    @Test
    void importsFullTag() {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LineConsumer parser = CoverageTagParser.create(null, inputFile(), listener);

        final String coverageTag = "[" + "impl~name~1->dsn~covered~2>>utest]";
        parser.readLine(3, coverageTag);

        assertThat(listener.build(), equalTo(List.of(item(SpecificationItemId.parseId("impl~name~1"), 3,
                List.of("dsn~covered~2"), List.of("utest")))));
    }

    @Test
    void importsConfiguredShortTag() {
        final PathConfig config = pathConfig();
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LineConsumer parser = CoverageTagParser.create(config, inputFile(), listener);

        parser.readLine(2, "[[covered:3]]");

        assertThat(listener.build(),
                equalTo(List.of(item(SpecificationItemId.createId("utest", "prefix.covered-1743877134"),
                        2, List.of("req~prefix.covered~3"), List.of()))));
    }

    private static PathConfig pathConfig() {
        return PathConfig.builder()
                .patternPathMatcher("glob:**")
                .coveredItemArtifactType("req")
                .coveredItemNamePrefix("prefix.")
                .tagArtifactType("utest")
                .build();
    }

    private static SpecificationItem item(final SpecificationItemId id, final int lineNumber,
            final List<String> coveredIds, final List<String> neededArtifactTypes) {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(id)
                .location(FILE, lineNumber);
        coveredIds.stream().map(SpecificationItemId::parseId).forEach(builder::addCoveredId);
        neededArtifactTypes.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }

    private static InputFile inputFile() {
        return StreamInput.forReader(Paths.get(FILE), new BufferedReader(new StringReader("")));
    }
}
