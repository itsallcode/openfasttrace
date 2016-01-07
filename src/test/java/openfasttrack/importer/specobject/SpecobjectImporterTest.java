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
import openfasttrack.core.SpecificationItemId;
import openfasttrack.matcher.SpecificationItemMatcher;

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
    public void testSingleSpecObject() throws FileNotFoundException
    {
        final List<SpecificationItem> result = runImporter("single-specobject.xml");
        assertThat(result, hasSize(1));
        final SpecificationItemId id = new SpecificationItemId.Builder().artifactType("doctype")
                .name("id").revision(42).build();
        final SpecificationItem expected = new SpecificationItem.Builder().id(id).comment("Comment")
                .rationale("Rationale").description("Description").build();
        assertThat(result.get(0), SpecificationItemMatcher.equalTo(expected));
    }

    private List<SpecificationItem> runImporter(final String fileName) throws FileNotFoundException
    {
        final FileReader reader = new FileReader(TEST_FILE_PREFIX + fileName);
        final SpecobjectImporter importer = new SpecobjectImporter(reader, this.xmlInputFactory);
        return importer.runImport();
    }
}
