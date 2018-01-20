package openfasttrack.importer.legacytag;

/*-
 * #%L
 * OpenFastTrack
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
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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
    }

    @Test(expected = ImporterException.class)
    public void testFactoryThrowsExceptionForMissingFile() throws IOException
    {
        final Importer importer = createImporter(asList(createConfig(PATH1)), Paths.get(PATH1));
        importer.runImport();
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
