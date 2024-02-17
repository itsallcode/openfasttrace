package org.itsallcode.openfasttrace.core.importer;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestMultiFileImporter
{
    private static final Path FOLDER = Paths.get("src/test/resources/markdown");
    private static final Path PATH1 = FOLDER.resolve("sample_design.md");
    private static final InputFile FILE1 = RealFileInput.forPath(PATH1);
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
        this.multiFileImporter = new MultiFileImporterImpl(this.specItemBuilderMock,
                this.factoryLoaderMock);

        lenient().when(this.factoryLoaderMock.supportsFile(any())).thenReturn(true);
        lenient().when(this.factoryLoaderMock.getImporterFactory(any()))
                .thenReturn(Optional.of(this.importerFactoryMock));
        lenient().when(this.importerFactoryMock.createImporter(any(), same(this.specItemBuilderMock)))
                .thenReturn(this.importerMock);
    }

    @Test
    void testImportSingleFile()
    {
        this.multiFileImporter.importFile(FILE1);
        verify(this.importerMock).runImport();
    }

    @Test
    void testImportAnySingleFile()
    {
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
        this.multiFileImporter.importAny(asList(FOLDER));
        verify(this.importerMock, times(2)).runImport();
    }

    // [itest->dsn~input-directory-recursive-traversal~1]
    @Test
    void testRecursiveDir()
    {
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
}
