package openfasttrack.importer.legacytag;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;

public class TestLegacyTagImporterFactory
{
    private static final String PATH1 = "path1";
    private static final String PATH2 = "path2";

    @Mock
    private ImportEventListener listenerMock;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFactoryWithEmptyPathConfigListSupportsNothing()
    {
        assertSupportsFile(emptyList(), PATH1, false);
    }

    @Test
    public void testFactorySupportsFile()
    {
        assertSupportsFile(asList(createConfig(PATH1)), PATH1, true);
    }

    @Test
    public void testFactoryDoesNotSupportsFile()
    {
        assertSupportsFile(asList(createConfig(PATH1)), PATH2, false);
    }

    @Test(expected = ImporterException.class)
    public void testFactoryThrowsExceptionForUnsupportedFile()
    {
        createImporter(asList(createConfig(PATH1)), Paths.get(PATH2));
    }

    @Test
    public void testFactoryCreatesImporterForSupportedFile() throws IOException
    {
        final File tempFile = this.temp.newFile();
        final Importer importer = createImporter(asList(createConfig(tempFile.getAbsolutePath())),
                tempFile.toPath());
        assertThat(importer, notNullValue());
        assertThat(importer, instanceOf(LegacyTagImporter.class));
    }

    @Test(expected = ImporterException.class)
    public void testFactoryThrowsExceptionForMissingFile() throws IOException
    {
        final Importer importer = createImporter(asList(createConfig(PATH1)), Paths.get(PATH1));
        assertThat(importer, notNullValue());
        assertThat(importer, instanceOf(LegacyTagImporter.class));
    }

    private void assertSupportsFile(final List<PathConfig> pathConfigs, final String path,
            final boolean expected)
    {
        assertThat(create(pathConfigs).supportsFile(Paths.get(path)), equalTo(expected));
    }

    private Importer createImporter(final List<PathConfig> pathConfigs, final Path path)
    {
        return create(pathConfigs).createImporter(path, StandardCharsets.UTF_8,
                this.listenerMock);
    }

    private LegacyTagImporterFactory create(final List<PathConfig> pathConfigs)
    {
        return new LegacyTagImporterFactory(pathConfigs);
    }

    private PathConfig createConfig(final String globPattern)
    {
        return new PathConfig("glob:" + globPattern);
    }
}
