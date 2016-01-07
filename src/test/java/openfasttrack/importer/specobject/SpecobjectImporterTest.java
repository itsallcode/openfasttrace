package openfasttrack.importer.specobject;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItem;

/**
 * Test for {@link SpecobjectImporter}
 */
public class SpecobjectImporterTest
{
    private static final String TEST_FILE_PREFIX = "src/test/resources/specobject/";
    private XMLInputFactory xmlInputFactory;

    @Before
    public void setUp()
    {
        this.xmlInputFactory = XMLInputFactory.newFactory();
    }

    @Test
    // @Ignore
    public void test() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter("single-specobject.xml");
        assertThat(result, hasSize(1));
    }

    private List<SpecificationItem> runImporter(final String fileName) throws FileNotFoundException
    {
        final FileReader reader = new FileReader(TEST_FILE_PREFIX + fileName);
        final SpecobjectImporter importer = new SpecobjectImporter(reader, this.xmlInputFactory);
        return importer.runImport();
    }
}
