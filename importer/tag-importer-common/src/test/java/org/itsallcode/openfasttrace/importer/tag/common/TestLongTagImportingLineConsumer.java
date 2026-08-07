package org.itsallcode.openfasttrace.importer.tag.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
