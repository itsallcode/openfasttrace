package openfasttrack.importer;

import static org.mockito.Mockito.verify;

import java.io.StringReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestImportMarkdown
{
    @Mock
    ImportEventListener listenerMock;
    final private static String REQ_ID = "type.id~1";
    final private static String REQ_TITLE = "Requirement Title";

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
    public void testFindRequirementId()
    {
        final String text = TestImportMarkdown.REQ_TITLE + "<a id=\"" + REQ_ID + "\"/>";
        final StringReader reader = new StringReader(text);
        new ImporterFactory();
        final Importer importer = ImporterFactory.createImporter(reader, this.listenerMock);
        importer.runImport();
        verify(this.listenerMock).foundNewSpecificationItem(REQ_ID);
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
