package org.itsallcode.openfasttrace.importer.tag;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.ImporterContext;
import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// [utest->dsn~import.short-coverage-tag~1]
@ExtendWith(MockitoExtension.class)
class TestTagImporterFactoryWithConfig
{
    private static final String PATH1 = "path1";
    private static final String PATH2 = "path2";

    @Mock
    private ImportEventListener listenerMock;
    @Mock
    private ImporterContext contextMock;

    @Test
    void testFactoryWithEmptyPathConfigListSupportsNothing()
    {
        assertSupportsFile(configure(), PATH1, false);
    }

    @Test
    void testFactorySupportsFile()
    {
        assertSupportsFile(configure(glob(PATH1)), PATH1, true);
    }

    @Test
    void testFactoryDoesNotSupportFileWithWrongBasePath()
    {
        assertSupportsFile(configure(glob("path")), "base1/path", false);
    }

    @Test
    void testFactoryDoesNotSupportFileWithWrongPath()
    {
        assertSupportsFile(configure(glob("path")), "base/path1", false);
    }

    @Test
    void testFactoryDoesNotSupportsFile()
    {
        assertSupportsFile(configure(glob(PATH1)), PATH2, false);
    }

    @Test
    void testFactoryForUnsupportedFileThrowsException()
    {
        final ImportSettings settings = configure(glob(PATH1));
        final Path path = Paths.get(PATH2);
        assertThrows(ImporterException.class,
                () -> createImporter(settings, path));
    }

    @Test
    void testFactoryCreatesImporterForSupportedFile(@TempDir final Path tempDir) throws IOException
    {
        final File tempFile = tempDir.resolve("test").toFile();
        final String glob = tempFile.getAbsolutePath().replace('\\', '/');
        final Importer importer = createImporter(configure(glob(glob)), tempFile.toPath());
        assertThat(importer, notNullValue());
    }

    @Test
    void testFactoryForMissingFileThrowsException() throws IOException
    {
        final Importer importer = createImporter(configure(glob(PATH1)), Paths.get(PATH1));
        assertThrows(ImporterException.class, importer::runImport);
    }

    private void assertSupportsFile(final ImportSettings settings, final String path,
            final boolean expected)
    {
        final InputFile file = RealFileInput.forPath(Paths.get(path));
        assertThat(create(settings).supportsFile(file), equalTo(expected));
    }

    private Importer createImporter(final ImportSettings settings, final Path path)
    {
        final InputFile file = RealFileInput.forPath(path);
        return create(settings).createImporter(file, this.listenerMock);
    }

    private ImportSettings configure(final PathConfig... pathConfigs)
    {
        return ImportSettings.builder().pathConfigs(asList(pathConfigs)).build();
    }

    private TagImporterFactory create(final ImportSettings settings)
    {
        final TagImporterFactory factory = new TagImporterFactory();
        factory.init(this.contextMock);
        when(this.contextMock.getImportSettings()).thenReturn(settings);
        return factory;
    }

    private PathConfig glob(final String globPattern)
    {
        return PathConfig.builder() //
                .patternPathMatcher("glob:" + globPattern) //
                .coveredItemArtifactType("") //
                .tagArtifactType("") //
                .build();
    }
}
