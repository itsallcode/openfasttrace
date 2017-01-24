package openfasttrack.importer.tag;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.SpecificationListBuilder;

/**
 * Test for {@link TagImporter}
 */
public class TestTagImporter
{
    private static final SpecificationItemId ID1 = id("artifactTypeA", "name1", 1);
    private static final SpecificationItemId ID2 = id("artifactTypeB", "name2.suffix", 2);
    private static final SpecificationItemId ID3 = id("artifactTypeC", "prefix.name3", 3);

    private static final String ID1_TEXT = "artifactTypeA~name1~1";
    private static final String ID2_TEXT = "artifactTypeB~name2.suffix~2";
    private static final String ID3_TEXT = "artifactTypeC~prefix.name3~3";

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
        assertItems(tag(ID1_TEXT), //
                item(ID1));
    }

    @Test
    public void testSingleTagTrailingNewline()
    {
        assertItems(tag(ID1_TEXT) + UNIX_NEWLINE, //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBefore()
    {
        assertItems("data before" + tag(ID1_TEXT), //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeWithSpaceSeparator()
    {
        assertItems("data before " + tag(ID1_TEXT), //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataAfter()
    {
        assertItems(tag(ID1_TEXT) + "data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataAfterWithSpaceSeparator()
    {
        assertItems(tag(ID1_TEXT) + " data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfter()
    {
        assertItems("data before" + tag(ID1_TEXT) + "data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfterWithSpaceSeparator()
    {
        assertItems("data before " + tag(ID1_TEXT) + " data after", //
                item(ID1));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithoutSpace()
    {
        assertItems(tag(ID1_TEXT) + "separator" + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithSpace()
    {
        assertItems(tag(ID1_TEXT) + " separator " + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSpaceSeparator()
    {
        assertItems(tag(ID1_TEXT) + " " + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithoutSeparator()
    {
        assertItems(tag(ID1_TEXT) + "" + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testThreeTagsOnOneLine()
    {
        assertItems(tag(ID1_TEXT) + tag(ID2_TEXT) + tag(ID3_TEXT), //
                item(ID1), item(ID2), item(ID3));
    }

    @Test
    public void testLinesSeparatedWithUnixNewLine()
    {
        assertItems(tag(ID1_TEXT) + UNIX_NEWLINE + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testLinesSeparatedWithWindowsNewLine()
    {
        assertItems(tag(ID1_TEXT) + WINDOWS_NEWLINE + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testLinesSeparatedWithCarriageReturn()
    {
        assertItems(tag(ID1_TEXT) + CARRIAGE_RETURN + tag(ID2_TEXT), //
                item(ID1), item(ID2));
    }

    @Test
    public void testDuplicateId()
    {
        assertItems(tag(ID1_TEXT) + tag(ID1_TEXT), //
                item(ID1), item(ID1));
    }

    @Test
    public void testDuplicateIdMultipleLines()
    {
        assertItems(tag(ID1_TEXT) + UNIX_NEWLINE + tag(ID1_TEXT), //
                item(ID1), item(ID1));
    }

    private String tag(final String id)
    {
        return "[" + id + "]";
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

    private static SpecificationItem item(final SpecificationItemId id)
    {
        return new SpecificationItem.Builder().id(id).build();
    }

    private void assertItems(final String content, final SpecificationItem... expectedItems)
    {
        final List<SpecificationItem> actual = runImporter(content);
        assertThat(actual, hasSize(expectedItems.length));
        if (expectedItems.length > 0)
        {
            assertThat(actual, contains(expectedItems));
        }
    }

    private List<SpecificationItem> runImporter(final String content)
    {
        final SpecificationListBuilder builder = new SpecificationListBuilder();
        new TagImporterFactory().createImporter("testfilename", new StringReader(content), builder)
                .runImport();
        return builder.build();
    }
}
