package openfasttrack.importer.tag;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.SpecificationMapListBuilder;

/**
 * Test for {@link TagImporter}
 */
public class TestTagImporter
{
    private static final SpecificationItemId ID1 = id("artifactTypeA", "name1", 1);
    private static final SpecificationItemId ID2 = id("artifactTypeB", "name2.suffix", 2);
    private static final SpecificationItemId ID3 = id("artifactTypeC", "prefix.name3", 3);

    private static final String UNIX_NEWLINE = "\n";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String WINDOWS_NEWLINE = CARRIAGE_RETURN + UNIX_NEWLINE;

    @Before
    public void setUp() throws Exception
    {
    }

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
        assertItems(tag(ID1), //
                item(ID1));
    }

    @Test
    public void testSingleTagTrailingNewline()
    {
        assertItems(tag(ID1) + UNIX_NEWLINE, //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBefore()
    {
        assertItems("data before" + tag(ID1), //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeWithSpaceSeparator()
    {
        assertItems("data before " + tag(ID1), //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataAfter()
    {
        assertItems(tag(ID1) + "data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataAfterWithSpaceSeparator()
    {
        assertItems(tag(ID1) + " data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfter()
    {
        assertItems("data before" + tag(ID1) + "data after", //
                item(ID1));
    }

    @Test
    public void testSingleTagWithDataBeforeAndAfterWithSpaceSeparator()
    {
        assertItems("data before " + tag(ID1) + " data after", //
                item(ID1));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithoutSpace()
    {
        assertItems(tag(ID1) + "separator" + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSeparatorWithSpace()
    {
        assertItems(tag(ID1) + " separator " + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithSpaceSeparator()
    {
        assertItems(tag(ID1) + " " + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testMultipleTagsPerLineWithoutSeparator()
    {
        assertItems(tag(ID1) + "" + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testThreeTagsOnOneLine()
    {
        assertItems(tag(ID1) + tag(ID2) + tag(ID3), //
                item(ID1), item(ID2), item(ID3));
    }

    @Test
    public void testLinesSeparatedWithUnixNewLine()
    {
        assertItems(tag(ID1) + UNIX_NEWLINE + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testLinesSeparatedWithWindowsNewLine()
    {
        assertItems(tag(ID1) + WINDOWS_NEWLINE + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testLinesSeparatedWithCarriageReturn()
    {
        assertItems(tag(ID1) + CARRIAGE_RETURN + tag(ID2), //
                item(ID1), item(ID2));
    }

    @Test
    public void testDuplicateId()
    {
        assertItems(tag(ID1) + tag(ID1), //
                item(ID1));
    }

    @Test
    public void testDuplicateIdMultipleLines()
    {
        assertItems(tag(ID1) + UNIX_NEWLINE + tag(ID1), //
                item(ID1));
    }

    private String tag(final SpecificationItemId id)
    {
        return "[" + id.toString() + "]";
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
        assertThat(actual, contains(expectedItems));
    }

    private List<SpecificationItem> runImporter(final String content)
    {
        final SpecificationMapListBuilder builder = new SpecificationMapListBuilder();
        new TagImporterFactory().createImporter(new StringReader(content), builder).runImport();
        return builder.build();
    }
}
