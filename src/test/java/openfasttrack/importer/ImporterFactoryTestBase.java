package openfasttrack.importer;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Base class for {@link ImporterFactory} tests.
 *
 * @param <T>
 *            type of the factory under test
 */
public abstract class ImporterFactoryTestBase<T extends ImporterFactory>
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSupportedFileNames()
    {
        assertSupported(getSupportedFilenames(), true);
    }

    @Test
    public void testUnsupportedFileNames()
    {
        assertSupported(getUnsupportedFilenames(), false);
    }

    @Test
    public void testCreateImporterThrowsExceptionForUnsupportedFilenames()
    {
        final Path unsupportedPath = Paths.get("dir", getUnsupportedFilenames().get(0));
        this.thrown.expect(ImporterException.class);
        this.thrown.expectMessage("File '" + unsupportedPath + "' not supported for import");
        createFactory().createImporter(unsupportedPath, null, null);
    }

    @Test
    public void testCreateImporterThrowsExceptionForMissingFile()
    {
        final Path supportedPath = Paths.get("dir", getSupportedFilenames().get(0));
        this.thrown.expect(ImporterException.class);
        this.thrown.expectMessage("Error reading file '" + supportedPath + "'");
        createFactory().createImporter(supportedPath, StandardCharsets.UTF_8, null);
    }

    private void assertSupported(final List<String> filenames, final boolean expectedResult)
    {
        final T factory = createFactory();
        for (final String filename : filenames)
        {
            final Path path = Paths.get("dir", filename);
            assertThat(path.toString(), factory.supportsFile(path), equalTo(expectedResult));
        }
    }

    protected abstract T createFactory();

    protected abstract List<String> getSupportedFilenames();

    protected abstract List<String> getUnsupportedFilenames();
}
