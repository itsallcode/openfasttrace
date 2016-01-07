package openfasttrack.importer.markdown;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;

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
    final private static SpecificationItemId ID = SpecificationItemId.parseId("type.id~1");
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

    @Test
    public void testIdentifyTitle()
    {
        assertMatch(MdPattern.TITLE, "#Title", "# Title", "###### Title", "#   Title");
        assertMismatch(MdPattern.TITLE, "Title", "Title #", " # Title");
    }

    private void assertMatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, MdPattern.TITLE, true);
    }

    private void assertMismatch(final MdPattern mdPattern, final String... samples)
    {
        assertMatching(samples, MdPattern.TITLE, false);
    }

    private void assertMatching(final String[] samples, final MdPattern mdPattern,
            final boolean mustMatch)
    {
        for (final String text : samples)
        {
            final Matcher matcher = mdPattern.getPattern().matcher(text);
            assertThat(mdPattern.toString() + " must " + (mustMatch ? "" : "not") + "match",
                    matcher.matches(), equalTo(mustMatch));
        }
    }

    @Test
    public void testFindRequirementId()
    {
        final String text = "# " + TestImportMarkdown.TITLE + "<a id=\"" + ID + "\"/>";
        final StringReader reader = new StringReader(text);
        new ImporterFactory();
        final Importer importer = ImporterFactory.createImporter(reader, this.listenerMock);
        importer.runImport();
        final InOrder inOrder = inOrder(this.listenerMock);
        inOrder.verify(this.listenerMock).startSpecificationItem();
        inOrder.verify(this.listenerMock).setId(ID);
        inOrder.verifyNoMoreInteractions();
    }

    // @Test
    // public void testFindRequirementIdPulsTitle()
    // {
    // final String text = TestImportMarkdown.REQ_TITLE + "<a id=\"" + REQ_ID +
    // "\"/>";
    // final StringReader reader = new StringReader(text);
    // new ImporterFactory();
    // final Importer importer = ImporterFactory.createImporter(reader,
    // this.listenerMock);
    // importer.runImport();
    // verify(this.listenerMock).foundNewSpecificationItem(REQ_ID, REQ_TITLE);
    // }
}
