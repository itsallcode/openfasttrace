package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class TestLongTagImportingLineConsumer
{
    private static final String FILE = "source.file";

    static Stream<Arguments> tagImportingTests()
    {
        final String namedTag = "[impl~tag~1 -> dsn~first~2, dsn~second~3 >> utest, itest" + "]";
        final String generatedNameTag = "[impl -> dsn~covered~2" + "]";
        final String readableNamesTag = "[impl -> dsn~first~2, dsn~second~3 >> utest" + "]";
        return Stream.of(
                // [utest->dsn~import.full-coverage-tag-with-name-and-revision~1]
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
                // [utest->dsn~import.full-coverage-tag-multiple-needed-coverage~1]
                testCase(3, namedTag,
                        item("impl~tag~1", 3, namedTag, List.of("dsn~first~2", "dsn~second~3"),
                                List.of("utest", "itest"))),
                // [utest->dsn~import.full-coverage-tag~1]
                testCase(4, generatedNameTag,
                        item("impl~covered-3014110766~0", 4, generatedNameTag, List.of("dsn~covered~2"),
                                List.of())),
                // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
                testCase(5, readableNamesTag,
                        item("impl~first~0", 5, readableNamesTag, List.of("dsn~first~2"), List.of("utest")),
                        item("impl~second~0", 5, readableNamesTag, List.of("dsn~second~3"), List.of("utest"))));
    }

    static Arguments testCase(final int lineNumber, final String tag, final SpecificationItem... expectedItems)
    {
        return Arguments.of(lineNumber, tag, List.of(expectedItems));
    }

    @ParameterizedTest
    @MethodSource("tagImportingTests")
    void importsLongTag(final int lineNumber, final String tag, final List<SpecificationItem> expectedItems)
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LongTagImportingLineConsumer consumer = new LongTagImportingLineConsumer(inputFile(), listener);
        consumer.readLine(lineNumber, tag);
        assertThat(listener.build(), equalTo(expectedItems));
    }

    // [utest->dsn~located-specification-item-id-tag-ranges~1]
    @Test
    void importsLocatedLongTagIds()
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        new LongTagImportingLineConsumer(inputFile(), listener).readLine(3,
                "😀 [impl~tag~1 -> dsn~covered~2" + "]");

        final SpecificationItem item = listener.build().get(0);

        assertAll(
                () -> assertThat(item.getLocatedId().getRange(), is(range(2, 4, 14))),
                () -> assertThat(item.getLocatedCoveredIds().get(0).getRange(), is(range(2, 18, 31))));
    }

    // [utest->dsn~located-specification-item-id-tag-ranges~1]
    @Test
    void importsLocatedLongTagIdWithGeneratedName()
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        new LongTagImportingLineConsumer(inputFile(), listener).readLine(3,
                "😀 [impl -> dsn~covered~2" + "]");

        final SpecificationItem item = listener.build().get(0);

        assertAll(
                () -> assertThat(item.getLocatedId().getRange(), is(nullValue())),
                () -> assertThat(item.getLocatedCoveredIds().get(0).getRange(), is(range(2, 12, 25))));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @ParameterizedTest
    @ValueSource(strings = { "--", "//", "#", " *", "'", ";", "%", "!", "" })
    void importsMultiLineTagWithCommentMarker(final String marker)
    {
        final List<SpecificationItem> items = importMultiLine(
                marker + " [" + "impl~tag~1->dsn~first~1,",
                marker + "                 dsn~second~2,",
                marker + "                 dsn~third~3" + "]");

        assertThat(items, hasSize(1));
        final SpecificationItem item = items.get(0);
        assertAll(
                () -> assertThat(item.getId(), equalTo(SpecificationItemId.parseId("impl~tag~1"))),
                () -> assertThat(item.getCoveredIds(), contains(SpecificationItemId.parseId("dsn~first~1"),
                        SpecificationItemId.parseId("dsn~second~2"), SpecificationItemId.parseId("dsn~third~3"))),
                () -> assertThat(item.getLocation().getLine(), equalTo(1)));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @Test
    void importsMultiLineTagWithNeededCoverageOnLastLine()
    {
        final List<SpecificationItem> items = importMultiLine(
                "-- [" + "impl~tag~1 -> dsn~first~2,",
                "--                 dsn~second~3 >> utest, itest" + "]");

        assertThat(items, hasSize(1));
        assertAll(
                () -> assertThat(items.get(0).getCoveredIds(), contains(SpecificationItemId.parseId("dsn~first~2"),
                        SpecificationItemId.parseId("dsn~second~3"))),
                () -> assertThat(items.get(0).getNeedsArtifactTypes(), contains("utest", "itest")));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @Test
    void importsUnnamedMultiLineTagAsOneItemPerCoveredId()
    {
        final List<SpecificationItem> items = importMultiLine(
                "// [" + "impl -> dsn~first~2,",
                "//           dsn~second~3 >> utest" + "]");

        assertThat(items.stream().map(SpecificationItem::getId).toList(),
                contains(SpecificationItemId.parseId("impl~first~0"), SpecificationItemId.parseId("impl~second~0")));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    // [utest->dsn~located-specification-item-id-tag-ranges~1]
    @Test
    void importsLocatedMultiLineTagIds()
    {
        final String secondLine = "//                  dsn~second~3" + "]";
        final List<SpecificationItem> items = importMultiLine(
                "// [" + "impl~tag~1 -> dsn~first~2,",
                secondLine);

        final SpecificationItem item = items.get(0);
        final int secondStart = secondLine.indexOf("dsn~second~3");
        assertAll(
                () -> assertThat(item.getLocatedId().getRange(), is(range(0, 4, 14))),
                () -> assertThat(item.getLocatedCoveredIds().get(0).getRange(), is(range(0, 18, 29))),
                () -> assertThat(item.getLocatedCoveredIds().get(1).getRange(),
                        is(range(1, secondStart, secondStart + 12))));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @Test
    void importsSingleLineTagsAroundMultiLineTag()
    {
        final List<SpecificationItem> items = importMultiLine(
                "[" + "impl->dsn~before~1] [" + "impl~tag~1->dsn~first~1,",
                "dsn~second~1] [" + "impl~after~1->dsn~after~1" + "]");

        assertThat(items, hasSize(3));
        assertAll(
                () -> assertThat(items.get(0).getCoveredIds(), contains(SpecificationItemId.parseId("dsn~before~1"))),
                () -> assertThat(items.get(1).getId(), equalTo(SpecificationItemId.parseId("impl~tag~1"))),
                () -> assertThat(items.get(2).getId(), equalTo(SpecificationItemId.parseId("impl~after~1"))),
                () -> assertThat(items.get(2).getLocation().getLine(), equalTo(2)));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @Test
    void rejectsInvalidContinuationLine()
    {
        final LongTagImportingLineConsumer consumer = multiLineConsumer(SpecificationListBuilder.create());
        consumer.readLine(1, "-- [" + "impl~tag~1->dsn~first~1,");
        final ImporterException exception = assertThrows(ImporterException.class,
                () -> consumer.readLine(2, "-- not an ID"));
        assertThat(exception.getMessage(), containsString("started at line 1"));
    }

    // [utest->dsn~import.full-coverage-tag-multi-line~1]
    @Test
    void rejectsUnterminatedMultiLineTagAtEndOfFile()
    {
        final LongTagImportingLineConsumer consumer = multiLineConsumer(SpecificationListBuilder.create());
        consumer.readLine(1, "-- [" + "impl~tag~1->dsn~first~1,");
        consumer.readLine(2, "--                dsn~second~1,");
        final ImporterException exception = assertThrows(ImporterException.class, consumer::finish);
        assertThat(exception.getMessage(), containsString("not terminated"));
    }

    @Test
    void ignoresUnclosedTagWithoutTrailingComma()
    {
        assertThat(importMultiLine("-- [" + "impl~tag~1->dsn~first~1", "-- dsn~second~1" + "]"), empty());
    }

    @Test
    void ignoresMultiLineTagWhenDisabled()
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LongTagImportingLineConsumer consumer = new LongTagImportingLineConsumer(inputFile(), listener);
        consumer.readLine(1, "-- [" + "impl~tag~1->dsn~first~1,");
        consumer.readLine(2, "--                dsn~second~1" + "]");
        consumer.finish();
        assertThat(listener.build(), empty());
    }

    private static List<SpecificationItem> importMultiLine(final String... lines)
    {
        final SpecificationListBuilder listener = SpecificationListBuilder.create();
        final LongTagImportingLineConsumer consumer = multiLineConsumer(listener);
        for (int i = 0; i < lines.length; i++)
        {
            consumer.readLine(i + 1, lines[i]);
        }
        consumer.finish();
        return listener.build();
    }

    private static LongTagImportingLineConsumer multiLineConsumer(final SpecificationListBuilder listener)
    {
        return new LongTagImportingLineConsumer(inputFile(), listener, true);
    }

    private static SourceRange range(final int line, final int start, final int end)
    {
        return new SourceRange(new SourcePosition(line, start), new SourcePosition(line, end));
    }

    private static SpecificationItem item(final String id, final int lineNumber, final String tag,
            final List<String> coveredIds,
            final List<String> neededArtifactTypes)
    {
        final SpecificationItemId specificationItemId = SpecificationItemId.parseId(id);
        final SpecificationItem.Builder builder = SpecificationItem.builder().location(FILE, lineNumber);
        if (tag.contains(id))
        {
            builder.id(locatedId(lineNumber, tag.indexOf(id), specificationItemId));
        }
        else
        {
            builder.id(specificationItemId);
        }
        int searchStart = 0;
        for (final String coveredId : coveredIds)
        {
            final int start = tag.indexOf(coveredId, searchStart);
            searchStart = start + coveredId.length();
            builder.addCoveredId(locatedId(lineNumber, start, SpecificationItemId.parseId(coveredId)));
        }
        neededArtifactTypes.forEach(builder::addNeedsArtifactType);
        return builder.build();
    }

    private static LocatedSpecificationItemId locatedId(final int lineNumber, final int start,
            final SpecificationItemId id)
    {
        final String text = id.toString();
        final int typeEnd = text.indexOf('~');
        final int revisionStart = text.lastIndexOf('~') + 1;
        return LocatedSpecificationItemId.builder().id(id).range(range(lineNumber - 1, start, start + text.length()))
                .artifactTypeRange(range(lineNumber - 1, start, start + typeEnd))
                .nameRange(range(lineNumber - 1, start + typeEnd + 1, start + revisionStart - 1))
                .revisionRange(range(lineNumber - 1, start + revisionStart, start + text.length())).build();
    }

    private static InputFile inputFile()
    {
        return StreamInput.forReader(Paths.get(FILE), new BufferedReader(new StringReader("")));
    }
}
