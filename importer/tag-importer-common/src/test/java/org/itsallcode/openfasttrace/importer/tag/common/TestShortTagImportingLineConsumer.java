package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// [utest->dsn~import.short-coverage-tag~1]
class TestShortTagImportingLineConsumer
{
    private static final String FILE = "source.file";

    static Stream<Arguments> shortTagImportingTests()
    {
        return Stream.of(
                testCase(3, "[[covered:2" + "]]", null,
                        item("covered-3798966306", 3, "req~covered~2")),
                testCase(4, "[[covered:2" + "]]", "prefix.",
                        item("prefix.covered-1644633624", 4, "req~prefix.covered~2")),
                testCase(5, "[[first:2" + "]]" + "[[second:3" + "]]", null,
                        item("first-969050621", 5, "req~first~2"),
                        item("second-2680780004", 5, "req~second~3")));
    }

    private static Arguments testCase(final int lineNumber, final String tag, final String coveredItemNamePrefix,
            final SpecificationItem... expectedItems)
    {
        return Arguments.of(lineNumber, tag, coveredItemNamePrefix, List.of(expectedItems));
    }

    @ParameterizedTest
    @MethodSource("shortTagImportingTests")
    void importsShortTag(final int lineNumber, final String tag, final String coveredItemNamePrefix,
            final List<SpecificationItem> expectedItems)
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final ShortTagImportingLineConsumer consumer = new ShortTagImportingLineConsumer(
                pathConfig(coveredItemNamePrefix), inputFile(), listener);

        consumer.readLine(lineNumber, tag);

        assertThat(listener.build(), equalTo(expectedItems));
    }

    // [utest->dsn~located-specification-item-id-tag-ranges~1]
    @Test
    void importsLocatedShortTagCoveredId()
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        new ShortTagImportingLineConsumer(pathConfig(null), inputFile(), listener).readLine(3, "😀 [[covered:2]]");

        final LocatedSpecificationItemId coveredId = listener.build().get(0).getLocatedCoveredIds().get(0);

        assertAll(
                () -> assertThat(coveredId.getId(), is(SpecificationItemId.parseId("req~covered~2"))),
                () -> assertThat(coveredId.getRange(), is(range(2, 5, 14))),
                () -> assertThat(coveredId.getArtifactTypeRange().isEmpty(), is(true)),
                () -> assertThat(coveredId.getNameRange().orElseThrow(), is(range(2, 5, 12))),
                () -> assertThat(coveredId.getRevisionRange().orElseThrow(), is(range(2, 13, 14))));
    }

    private static SourceRange range(final int line, final int start, final int end)
    {
        return new SourceRange(new SourcePosition(line, start), new SourcePosition(line, end));
    }

    private static PathConfig pathConfig(final String coveredItemNamePrefix)
    {
        return PathConfig.builder()
                .patternPathMatcher("glob:**")
                .coveredItemArtifactType("req")
                .coveredItemNamePrefix(coveredItemNamePrefix)
                .tagArtifactType("utest")
                .build();
    }

    private static SpecificationItem item(final String tagItemName, final int lineNumber, final String coveredId)
    {
        return SpecificationItem.builder()
                .id(SpecificationItemId.createId("utest", tagItemName))
                .location(FILE, lineNumber)
                .addCoveredId(SpecificationItemId.parseId(coveredId))
                .build();
    }

    private static InputFile inputFile()
    {
        return StreamInput.forReader(Paths.get(FILE), new BufferedReader(new StringReader("")));
    }
}
