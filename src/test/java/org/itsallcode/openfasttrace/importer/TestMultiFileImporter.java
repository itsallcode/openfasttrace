package org.itsallcode.openfasttrace.importer;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;

import com.github.hamstercommunity.matcher.auto.AutoMatcher;

class TestMultiFileImporter
{
    private static final Path FOLDER = Paths.get("src/test/resources/markdown");
    private static final Path PATH2 = FOLDER.resolve("sample_system_requirements.md");
    private static final Path PATH1 = FOLDER.resolve("sample_design.md");
    private static final InputFile FILE1 = InputFile.forPath(PATH1);
    private static final InputFile FILE2 = InputFile.forPath(PATH2);
    private static final Path NON_EXISTING_FILE = FOLDER.resolve("does_not_exist");

    @Mock
    private SpecificationListBuilder specItemBuilderMock;
    @Mock
    private ImporterFactoryLoader factoryLoaderMock;
    @Mock
    private Importer importerMock;
    @Mock
    private ImporterFactory importerFactoryMock;

    private MultiFileImporter multiFileImporter;

    @BeforeEach
    void beforeEach()
    {
        MockitoAnnotations.initMocks(this);
        this.multiFileImporter = new MultiFileImporter(this.specItemBuilderMock,
                this.factoryLoaderMock);
    }

    @Test
    void testImportSingleFile()
    {
        expectFileImported(FILE1);
        this.multiFileImporter.importFile(FILE1);
        verify(this.importerMock).runImport();
    }

    @Test
    void testImportAnySingleFile()
    {
        expectFileImported(FILE1);
        this.multiFileImporter.importAny(asList(PATH1));
        verify(this.importerMock).runImport();
    }

    @Test
    void testImportAnyNonExistingFile()
    {
        this.multiFileImporter.importAny(asList(NON_EXISTING_FILE));
        verify(this.importerMock, times(0)).runImport();
    }

    @Test
    void testImportAnyFolderFile()
    {
        expectFileImported(FILE1);
        expectFileImported(FILE2);
        this.multiFileImporter.importAny(asList(FOLDER));
        verify(this.importerMock, times(2)).runImport();
    }

    // [itest->dsn~input-directory-recursive-traversal~1]
    @Test
    void testRecursiveDir()
    {
        expectFileImported(FILE1);
        expectFileImported(FILE2);
        this.multiFileImporter.importRecursiveDir(FOLDER, "**/*.md");
        verify(this.importerMock, times(2)).runImport();
    }

    @Test
    void testGetImportedItems()
    {
        final List<SpecificationItem> expected = new ArrayList<>();
        when(this.specItemBuilderMock.build()).thenReturn(expected);

        assertThat(this.multiFileImporter.getImportedItems(), sameInstance(expected));
    }

    private void expectFileImported(final InputFile file)
    {
        when(this.factoryLoaderMock.supportsFile(autoEqualTo(file))).thenReturn(true);
        when(this.factoryLoaderMock.getImporterFactory(autoEqualTo(file)))
                .thenReturn(this.importerFactoryMock);
        when(this.importerFactoryMock.createImporter(autoEqualTo(file),
                same(this.specItemBuilderMock))).thenReturn(this.importerMock);
    }

    private InputFile autoEqualTo(final InputFile file)
    {
        final HamcrestArgumentMatcher<InputFile> argMatcher = new HamcrestArgumentMatcher<>(
                AutoMatcher.equalTo(file));
        return argThat(argMatcher);
    }
}
