package openfasttrack.importer.specobject;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.importer.ImporterService;

/**
 * Test for {@link SpecobjectImporter}
 */
public class SpecobjectImporterTest
{
    private static final String TEST_FILE_PREFIX = "src/test/resources/specobject/";
    private SpecificationItem specItem1;
    private SpecificationItem specItem2;

    @Before
    public void setup()
    {
        this.specItem1 = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id") //
                        .revision(42) //
                        .build()) //
                .comment("Comment").rationale("Rationale") //
                .description("Description") //
                .addNeededArtifactType("code").addNeededArtifactType("test") //
                .addCoveredId(new SpecificationItemId.Builder() //
                        .artifactType(null) //
                        .name("provid") //
                        .revision(43) //
                        .build()) //
                .addDependOnId(new SpecificationItemId.Builder() //
                        .artifactType("dependsOnDocType") //
                        .name("dependsOnName") //
                        .revision(44) //
                        .build()) //
                .build();
        this.specItem2 = new SpecificationItem.Builder() //
                .id(new SpecificationItemId.Builder() //
                        .artifactType("doctype") //
                        .name("id2") //
                        .revision(42 + 1) //
                        .build()) //
                .comment("Comment2").rationale("Rationale2") //
                .description("Description2") //
                .build();
    }

    @Test
    public void testSingleSpecObject() throws FileNotFoundException
    {
        final Map<SpecificationItemId, SpecificationItem> result = runImporter(
                "single-specobject.xml");
        assertThat(result.values(), hasSize(1));
        assertThat(result, hasEntry(this.specItem1.getId(), this.specItem1));
    }

    @Test
    public void testTwoSpecObjects() throws FileNotFoundException
    {
        final Map<SpecificationItemId, SpecificationItem> result = runImporter(
                "two-specobjects.xml");
        assertThat(result.values(), hasSize(2));
        assertThat(result, hasEntry(this.specItem1.getId(), this.specItem1));
        assertThat(result, hasEntry(this.specItem2.getId(), this.specItem2));
    }

    @Test
    public void testEmptySpecObjects() throws FileNotFoundException
    {
        final Map<SpecificationItemId, SpecificationItem> result = runImporter("no-specobject.xml");
        assertThat(result.values(), hasSize(0));
    }

    private Map<SpecificationItemId, SpecificationItem> runImporter(final String fileName)
            throws FileNotFoundException
    {
        final Path file = Paths.get(TEST_FILE_PREFIX, fileName).toAbsolutePath();
        return new ImporterService().importFile(file);
    }
}
