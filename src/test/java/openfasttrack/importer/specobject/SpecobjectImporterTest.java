package openfasttrack.importer.specobject;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.stream.XMLInputFactory;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.Importer;
import openfasttrack.importer.SpecificationItemListBuilder;
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
        final SpecificationItemId id = new SpecificationItemId.Builder() //
                .artifactType("doctype") //
                .name("id") //
                .revision(42) //
                .build();
        final SpecificationItemId coveredId = new SpecificationItemId.Builder() //
                .artifactType(null) //
                .name("provid") //
                .revision(43) //
                .build();
        final SpecificationItemId dependsOnId = new SpecificationItemId.Builder() //
                .artifactType("dependsOnDocType") //
                .name("dependsOnName") //
                .revision(44) //
                .build();
        final SpecificationItem expected = new SpecificationItem.Builder() //
                .id(id) //
                .comment("Comment").rationale("Rationale") //
                .description("Description") //
                .addNeededArtifactType("code").addNeededArtifactType("test") //
                .addCoveredId(coveredId) //
                .addDependOnId(dependsOnId) //
                .build();
        assertThat(result.get(0), SpecificationItemMatcher.equalTo(expected));
    }

    private List<SpecificationItem> runImporter(final String fileName) throws FileNotFoundException
    {
        final Path file = Paths.get(TEST_FILE_PREFIX, fileName).toAbsolutePath();
        final SpecificationItemListBuilder builder = new SpecificationItemListBuilder();
        final Importer importer = new SpecobjectImporterFactory().createImporter(file,
                StandardCharsets.UTF_8, builder);
        importer.runImport();
        return builder.build();
    }
}
