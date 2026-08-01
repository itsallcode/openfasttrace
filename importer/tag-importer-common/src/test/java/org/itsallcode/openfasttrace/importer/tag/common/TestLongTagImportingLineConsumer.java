package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestLongTagImportingLineConsumer {
    private static final String FILE = "source.file";

    static Stream<Arguments> tagImportingTests() {
        return Stream.of(
                // [utest->dsn~import.full-coverage-tag-with-name-and-revision~1]
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
                // [utest->dsn~import.full-coverage-tag-multiple-needed-coverage~1]
                testCase(3, "[impl~tag~1 -> dsn~first~2, dsn~second~3 >> utest, itest" + "]",
                        item("impl~tag~1", 3, List.of("dsn~first~2", "dsn~second~3"),
                                List.of("utest", "itest"))),
                // [utest->dsn~import.full-coverage-tag~1]
                testCase(4, "[impl -> dsn~covered~2" + "]",
                        item("impl~covered-3014110766~0", 4, List.of("dsn~covered~2"), List.of())),
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
                testCase(5, "[impl -> dsn~first~2, dsn~second~3 >> utest" + "]",
                        item("impl~first~0", 5, List.of("dsn~first~2"), List.of("utest")),
                        item("impl~second~0", 5, List.of("dsn~second~3"), List.of("utest"))));
    }

    static Arguments testCase(final int lineNumber, final String tag, final SpecificationItem... expectedItems) {
        return Arguments.of(lineNumber, tag, List.of(expectedItems));
    }

    @ParameterizedTest
    @MethodSource("tagImportingTests")
    void importsLongTag(final int lineNumber, final String tag, final List<SpecificationItem> expectedItems) {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LongTagImportingLineConsumer consumer = new LongTagImportingLineConsumer(inputFile(), listener);
        consumer.readLine(lineNumber, tag);
        assertThat(listener.build(), equalTo(expectedItems));
    }

    private static SpecificationItem item(final String id, final int lineNumber, final List<String> coveredIds,
            final List<String> neededArtifactTypes) {
        final SpecificationItem.Builder builder = SpecificationItem.builder()
                .id(SpecificationItemId.parseId(id))
                .location(FILE, lineNumber);
        coveredIds.stream().map(SpecificationItemId::parseId).forEach(builder::addCoveredId);
        neededArtifactTypes.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }

    private static InputFile inputFile() {
        return StreamInput.forReader(Paths.get(FILE), new BufferedReader(new StringReader("")));
    }
}
