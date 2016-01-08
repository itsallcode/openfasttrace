package openfasttrack.importer.markdown;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import java.io.StringReader;
import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterFactory;

@RunWith(MockitoJUnitRunner.class)
public class TestImportMarkdown
{
    @Mock
    ImportEventListener listenerMock;
    final private static SpecificationItemId ID = SpecificationItemId.parseId("type~id~1");
    final private static String TITLE = "Requirement Title";

    // @Test
    // public void readSpec() throws FileNotFoundException
    // {
    //
    // final FileReader fileReader = new FileReader(new
    // File("doc/system_requirements.md"));
    // final Reader reader = new BufferedReader(fileReader);
    // final Importer importer = ImporterFactory.createImporter(reader,
    // this.listenerMock);
    // importer.runImport();
    // }

    // [utest~md.specification_item_id_format~1]
    @Test
    public void testIdentifyId()
    {
        assertMatch(MdPattern.ID, "req~foo~1<a id=\"req~foo~1\"></a>", "a~b~0", "req~test~1",
                "req~test~999", "req~test.requirement~1", "req~test_underscore~1",
                "`req~test1~1`arbitrary text");
        assertMismatch(MdPattern.ID, "test~1", "req-test~1", "req~4test~1");
    }

    // [utest~md.specification_item_title~1]
    @Test
    public void testIdentifyTitle()
    {
        assertMatch(MdPattern.TITLE, "#Title", "# Title", "###### Title", "#   Title");
        assertMismatch(MdPattern.TITLE, "Title", "Title #", " # Title");
    }

    private void assertMatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, true);
    }

    private void assertMismatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, mdPattern, false);
    }

    private void assertMatching(final String[] samples, final MdPattern mdPattern,
            final boolean mustMatch)
    {
        for (final String text : samples)
        {
            final Matcher matcher = mdPattern.getPattern().matcher(text);
            assertThat(mdPattern.toString() + " must " + (mustMatch ? "" : "not ") + "match " + "\""
                    + text + "\"", matcher.matches(), equalTo(mustMatch));
        }
    }

    @Test
    public void testFindRequirement()
    {
        final StringBuilder builder = new StringBuilder("# ").append(TestImportMarkdown.TITLE)
                .append("\n") //
                .append("`").append(ID).append("` <a id=\"").append(ID).append("\"></a>");
        final StringReader reader = new StringReader(builder.toString());
        new ImporterFactory();
        final Importer importer = ImporterFactory.createImporter(reader, this.listenerMock);
        importer.runImport();
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).beginSpecificationItem();
        verify(this.listenerMock).setId(ID);
        verify(this.listenerMock).setTitle(TITLE);
        inOrder.verify(this.listenerMock).endSpecificationItem();
        inOrder.verifyNoMoreInteractions();
    }
}
