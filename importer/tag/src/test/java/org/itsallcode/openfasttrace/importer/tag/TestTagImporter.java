package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.CRC32;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem.Builder;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

// [utest->dsn~import.full-coverage-tag~1]
class TestTagImporter
{
    private static final String FILENAME = "testfilename.java";
    private static final SpecificationItemId ID1 = id("artifactTypeA", "name1", 1);
    private static final SpecificationItemId ID2 = id("artifactTypeB", "name2.suffix", 2);
    private static final SpecificationItemId ID3 = id("artifactTypeC", "prefix.name3", 3);

    private static final String ID1_TEXT = "artifactTypeA~name1~1";
    private static final String ID2_TEXT = "artifactTypeB~name2.suffix~2";
    private static final String ID3_TEXT = "artifactTypeC~prefix.name3~3";

    private static final String COVERING_ARTIFACT_TYPE1 = "coveringArtifactTypeX";
    private static final String COVERING_ARTIFACT_TYPE2 = "coveringArtifactTypeY";

    private static final String NEEDED_COVERAGE1 = "impl";
    private static final String NEEDED_COVERAGE2 = "test";

    private static final String UNIX_NEWLINE = "\n";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String WINDOWS_NEWLINE = CARRIAGE_RETURN + UNIX_NEWLINE;

    @Test
    void testEmptyString()
    {
        assertItems("");
    }

    @Test
    void testNonStringNoTag()
    {
        assertItems("non empty string");
    }

    @Test
    void testNonStringMultiLineStringNoTag()
    {
        assertItems("non empty string\nmultiline");
    }

    @Test
    void testSingleTag()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagTrailingNewline()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE, //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataBefore()
    {
        assertItems("data before" + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataBeforeWithSpaceSeparator()
    {
        assertItems("data before " + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataAfter()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataAfterWithSpaceSeparator()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataBeforeAndAfter()
    {
        assertItems("data before" + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testSingleTagWithDataBeforeAndAfterWithSpaceSeparator()
    {
        assertItems("data before " + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void testMultipleTagsPerLineWithSeparatorWithoutSpace()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "separator"
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    void testMultipleTagsPerLineWithSeparatorWithSpace()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " separator "
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    void testMultipleTagsPerLineWithSpaceSeparator()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " "
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    void testMultipleTagsPerLineWithoutSeparator()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + ""
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    void testThreeTagsOnOneLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT)
                        + tag(COVERING_ARTIFACT_TYPE1, ID3_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2),
                item(COVERING_ARTIFACT_TYPE1, 1, 2, ID3));
    }

    @Test
    void testLinesSeparatedWithUnixNewLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    void testLinesSeparatedWithWindowsNewLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + WINDOWS_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    void testLinesSeparatedWithCarriageReturn()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + CARRIAGE_RETURN
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    void testDuplicateId()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID1));
    }

    @Test
    void testDuplicateIdMultipleLines()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID1));
    }

    @Test
    void testDifferentArtifactTypes()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " "
                        + tag(COVERING_ARTIFACT_TYPE2, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE2, 1, 1, ID2));
    }

    @Test
    void tagWithExtraSpaces()
    {
        assertItems(
                "[ " + COVERING_ARTIFACT_TYPE1 + " -> " + ID1_TEXT + " ]", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void tagWithTabRecognized()
    {
        assertItems(
                "[" + COVERING_ARTIFACT_TYPE1 + "\t-> " + ID1_TEXT + "]", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    void tagWithNewlineNotRecognized()
    {
        assertItems("[" + COVERING_ARTIFACT_TYPE1 + "->\n" + ID1_TEXT + "]");
    }

    // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
    // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
    @Test
    void tagWithSingleRequiredCoverage()
    {
        assertItems("[" + COVERING_ARTIFACT_TYPE1 + "->" + ID1_TEXT + ">>" + NEEDED_COVERAGE1 + "]", //
                itemWithReadableName(COVERING_ARTIFACT_TYPE1, 1, ID1, List.of(NEEDED_COVERAGE1)));
    }

    // [utest->dsn~import.full-coverage-tag-with-needed-coverage~1]
    // [utest->dsn~import.full-coverage-tag-with-needed-coverage-readable-names~1]
    @Test
    void tagWithMultipleRequiredCoverage()
    {
        assertItems(
                "[" + COVERING_ARTIFACT_TYPE1 + "->" + ID1_TEXT + ">>" + NEEDED_COVERAGE1 + ","
                        + NEEDED_COVERAGE2
                        + "]", //
                itemWithReadableName(COVERING_ARTIFACT_TYPE1, 1, ID1, List.of(NEEDED_COVERAGE1, NEEDED_COVERAGE2)));
    }

    @Test
    void tagWithMultipleRequiredCoverageWithSpaces()
    {
        assertItems(
                "[ " + COVERING_ARTIFACT_TYPE1 + " -> " + ID1_TEXT + " >> " + NEEDED_COVERAGE1 + " , "
                        + NEEDED_COVERAGE2 + " ]", //
                itemWithReadableName(COVERING_ARTIFACT_TYPE1, 1, ID1, List.of(NEEDED_COVERAGE1, NEEDED_COVERAGE2)));
    }

    @Test
    void requiredCoverageIndicatorWithMissingTagIgnored()
    {
        assertItems("[" + COVERING_ARTIFACT_TYPE1 + "->" + ID1_TEXT + ">>]");
    }

    @Test
    void requiredCoverageWithSpaceIgnored()
    {
        assertItems("[ " + COVERING_ARTIFACT_TYPE1 + " -> " + ID1_TEXT + " >> tag with space ]");
    }

    private String tag(final String coveringArtifactType, final String coveredId)
    {
        return "[" + coveringArtifactType + "->" + coveredId + "]";
    }

    private static SpecificationItemId id(final String artifactType, final String name,
            final int revision)
    {
        return new SpecificationItemId.Builder() //
                .artifactType(artifactType) //
                .name(name) //
                .revision(revision) //
                .build();
    }

    private static SpecificationItem item(final String artifactType, final int lineNumber,
            final int counter, final SpecificationItemId coveredId)
    {
        return item(artifactType, lineNumber, counter, coveredId, emptyList());
    }

    private static SpecificationItem itemWithReadableName(final String artifactType, final int lineNumber,
            final SpecificationItemId coveredId, List<String> neededArtifactTypes)
    {
        final SpecificationItemId generatedId = SpecificationItemId.createId(artifactType,
                coveredId.getName(), 0);
        final Builder itemBuilder = SpecificationItem.builder() //
                .id(generatedId) //
                .addCoveredId(coveredId) //
                .location(FILENAME, lineNumber);
        neededArtifactTypes.forEach(itemBuilder::addNeedsArtifactType);
        return itemBuilder.build();
    }

    private static SpecificationItem item(final String artifactType, final int lineNumber,
            final int counter, final SpecificationItemId coveredId, List<String> neededArtifactTypes)
    {
        final SpecificationItemId generatedId = SpecificationItemId.createId(artifactType,
                generateName(coveredId, lineNumber, counter), 0);
        final Builder itemBuilder = SpecificationItem.builder() //
                .id(generatedId) //
                .addCoveredId(coveredId) //
                .location(FILENAME, lineNumber);
        neededArtifactTypes.forEach(itemBuilder::addNeedsArtifactType);
        return itemBuilder.build();
    }

    private static String generateName(final SpecificationItemId coveredId, final int lineNumber,
            final int counter)
    {
        final String uniqueName = FILENAME + lineNumber + counter + coveredId.toString();
        final CRC32 checksum = new CRC32();
        checksum.update(uniqueName.getBytes(StandardCharsets.UTF_8));
        return coveredId.getName() + "-" + checksum.getValue();
    }

    private void assertItems(final String content, final SpecificationItem... expectedItems)
    {
        final List<SpecificationItem> actual = runImporter(content);
        assertThat(actual, hasSize(expectedItems.length));
        if (expectedItems.length > 0)
        {
            assertThat(actual, AutoMatcher.contains(expectedItems));
            assertThat(actual, contains(expectedItems));
        }
    }

    private List<SpecificationItem> runImporter(final String content)
    {
        final SpecificationListBuilder builder = SpecificationListBuilder.create();
        final InputFile file = StreamInput.forReader(Paths.get(FILENAME),
                new BufferedReader(new StringReader(content)));
        final TagImporterFactory factory = new TagImporterFactory();
        factory.init(new ImporterContext(null));
        factory.createImporter(file, builder).runImport();
        return builder.build();
    }
}
