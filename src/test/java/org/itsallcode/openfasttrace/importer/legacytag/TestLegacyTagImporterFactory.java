package org.itsallcode.openfasttrace.importer.legacytag;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.ImporterException;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.legacytag.config.LegacyTagImporterConfig;
import org.itsallcode.openfasttrace.importer.legacytag.config.PathConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// [utest->dsn~import.short-coverage-tag~1]
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
        assertSupportsFile(configure(), PATH1, false);
    }

    @Test
    public void testFactorySupportsFile()
    {
        assertSupportsFile(configure(glob(PATH1)), PATH1, true);
    }

    @Test
    public void testFactorySupportsFileWithBasePath()
    {
        assertSupportsFile(configure("base", glob("path")), "base/path", true);
    }

    @Test
    public void testFactoryDoesNotSupportFileWithWrongBasePath()
    {
        assertSupportsFile(configure("base", glob("path")), "base1/path", false);
    }

    @Test
    public void testFactoryDoesNotSupportFileWithWrongPath()
    {
        assertSupportsFile(configure("base", glob("path")), "base/path1", false);
    }

    @Test
    public void testFactoryDoesNotSupportsFile()
    {
        assertSupportsFile(configure(glob(PATH1)), PATH2, false);
    }

    @Test(expected = ImporterException.class)
    public void testFactoryThrowsExceptionForUnsupportedFile()
    {
        createImporter(configure(glob(PATH1)), Paths.get(PATH2));
    }

    @Test
    public void testFactoryCreatesImporterForSupportedFile() throws IOException
    {
        final File tempFile = this.temp.newFile();
        final String glob = tempFile.getAbsolutePath().replace('\\', '/');
        final Importer importer = createImporter(configure(glob(glob)), tempFile.toPath());
        assertThat(importer, notNullValue());
    }

    @Test(expected = ImporterException.class)
    public void testFactoryThrowsExceptionForMissingFile() throws IOException
    {
        final Importer importer = createImporter(configure(glob(PATH1)), Paths.get(PATH1));
        importer.runImport();
    }

    private void assertSupportsFile(final LegacyTagImporterConfig config, final String path,
            final boolean expected)
    {
        final InputFile file = InputFile.createForPath(Paths.get(path));
        assertThat(create(config).supportsFile(file), equalTo(expected));
    }

    private Importer createImporter(final LegacyTagImporterConfig config, final Path path)
    {
        final InputFile file = InputFile.createForPath(path, StandardCharsets.UTF_8);
        return create(config).createImporter(file, this.listenerMock);
    }

    private LegacyTagImporterConfig configure(final String basePath,
            final PathConfig... pathConfigs)
    {
        final Optional<Path> optionalBasePath = Optional.ofNullable(basePath).map(Paths::get);
        return new LegacyTagImporterConfig(optionalBasePath, asList(pathConfigs));
    }

    private LegacyTagImporterConfig configure(final PathConfig... pathConfigs)
    {
        return configure(null, pathConfigs);
    }

    private LegacyTagImporterFactory create(final LegacyTagImporterConfig config)
    {
        return new LegacyTagImporterFactory(() -> config);
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
