package org.itsallcode.openfasttrace.importer.tag;

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
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.CRC32;

import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.SpecificationListBuilder;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Test;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

/**
 * Test for {@link TagImporter}
 */
// [utest->dsn~import.full-coverage-tag~1]
public class TestTagImporter
{
    private static final String FILENAME = "testfilename";
    private static final SpecificationItemId ID1 = id("artifactTypeA", "name1", 1);
    private static final SpecificationItemId ID2 = id("artifactTypeB", "name2.suffix", 2);
    private static final SpecificationItemId ID3 = id("artifactTypeC", "prefix.name3", 3);

    private static final String ID1_TEXT = "artifactTypeA~name1~1";
    private static final String ID2_TEXT = "artifactTypeB~name2.suffix~2";
    private static final String ID3_TEXT = "artifactTypeC~prefix.name3~3";

    private static final String COVERING_ARTIFACT_TYPE1 = "coveringArtifactTypeX";
    private static final String COVERING_ARTIFACT_TYPE2 = "coveringArtifactTypeY";

    private static final String UNIX_NEWLINE = "\n";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String WINDOWS_NEWLINE = CARRIAGE_RETURN + UNIX_NEWLINE;

    @Test
    public void testEmptyString()
    {
        assertItems("");
    }

    @Test
    public void testNonStringNoTag()
    {
        assertItems("non empty string");
    }

    @Test
    public void testNonStringMultiLineStringNoTag()
    {
        assertItems("non empty string\nmultiline");
    }

    @Test
    public void testSingleTag()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagTrailingNewline()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE, //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataBefore()
    {
        assertItems("data before" + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeWithSpaceSeparator()
    {
        assertItems("data before " + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataAfter()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataAfterWithSpaceSeparator()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfter()
    {
        assertItems("data before" + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfterWithSpaceSeparator()
    {
        assertItems("data before " + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " data after", //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithoutSpace()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + "separator"
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithSpace()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " separator "
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSpaceSeparator()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " "
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithoutSeparator()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + ""
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2));
    }

    @Test
    public void testThreeTagsOnOneLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT)
                        + tag(COVERING_ARTIFACT_TYPE1, ID3_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID2),
                item(COVERING_ARTIFACT_TYPE1, 1, 2, ID3));
    }

    @Test
    public void testLinesSeparatedWithUnixNewLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    public void testLinesSeparatedWithWindowsNewLine()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + WINDOWS_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    public void testLinesSeparatedWithCarriageReturn()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + CARRIAGE_RETURN
                        + tag(COVERING_ARTIFACT_TYPE1, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID2));
    }

    @Test
    public void testDuplicateId()
    {
        assertItems(tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 1, 1, ID1));
    }

    @Test
    public void testDuplicateIdMultipleLines()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + UNIX_NEWLINE
                        + tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE1, 2, 0, ID1));
    }

    @Test
    public void testDifferentArtifactTypes()
    {
        assertItems(
                tag(COVERING_ARTIFACT_TYPE1, ID1_TEXT) + " "
                        + tag(COVERING_ARTIFACT_TYPE2, ID2_TEXT), //
                item(COVERING_ARTIFACT_TYPE1, 1, 0, ID1), item(COVERING_ARTIFACT_TYPE2, 1, 1, ID2));
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
        final SpecificationItemId generatedId = SpecificationItemId.createId(artifactType,
                generateName(coveredId, lineNumber, counter), 0);
        return new SpecificationItem.Builder() //
                .id(generatedId) //
                .addCoveredId(coveredId) //
                .location(FILENAME, lineNumber) //
                .build();
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
        final InputFile file = InputFile.forReader(Paths.get(FILENAME),
                new BufferedReader(new StringReader(content)));
        new TagImporterFactory().createImporter(file, builder).runImport();
        return builder.build();
    }
}
