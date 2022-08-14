package org.itsallcode.openfasttrace.core.importer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.testutil.OsDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link ImporterServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
class TestImporterService
{
    @Mock
    private ImporterFactoryLoader factoryLoaderMock;
    @Mock
    private ImporterFactory importerFactoryMock;
    @Mock
    private Importer importerMock;
    @Mock
    private ImporterContext contextMock;

    @Captor
    private ArgumentCaptor<SpecificationListBuilder> builderArg;

    @Captor
    private ArgumentCaptor<InputFile> fileArg;

    private InputFile file;
    private ImporterService importerService;

    @BeforeEach
    void beforeEach()
    {
        this.importerService = new ImporterServiceImpl(this.factoryLoaderMock,
                ImportSettings.createDefault());
        this.file = RealFileInput.forPath(Paths.get("dir", "file"));

        when(this.factoryLoaderMock.getImporterFactory(any(InputFile.class)))
                .thenReturn(Optional.of(this.importerFactoryMock));
        when(this.importerFactoryMock.createImporter(any(), any())).thenReturn(this.importerMock);
    }

    @Test
    void testImportWindows()
    {
        OsDetector.assumeRunningOnWindows();
        runImporter();
        assertThat(this.fileArg.getValue().getPath(), equalTo("dir\\file"));
    }

    @Test
    void testImportUnix()
    {
        OsDetector.assumeRunningOnUnix();
        runImporter();
        assertThat(this.fileArg.getValue().getPath(), equalTo("dir/file"));
    }

    private void runImporter()
    {
        final List<SpecificationItem> result = this.importerService.importFile(this.file);

        verify(this.importerMock).runImport();
        verify(this.importerFactoryMock).createImporter(this.fileArg.capture(),
                this.builderArg.capture());

        final SpecificationListBuilder builder = this.builderArg.getValue();
        assertThat(result, sameInstance(builder.build()));
    }
}
