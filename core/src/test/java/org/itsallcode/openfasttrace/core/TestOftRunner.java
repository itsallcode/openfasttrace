package org.itsallcode.openfasttrace.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.core.exporter.ExporterService;
import org.itsallcode.openfasttrace.core.report.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestOftRunner
{
    private static final Path PATH = Paths.get("myPath");

    @Mock
    private ServiceFactory serviceFactoryMock;
    @Mock
    private ImporterService importerServiceMock;
    @Mock
    private MultiFileImporter multiFileImporterMock;
    @Mock
    private Linker linkerMock;
    @Mock
    private Trace traceMock;
    @Mock
    private Tracer tracerMock;
    @Mock
    private ExporterService exporterServiceMock;
    @Mock
    private ReportService reportServiceMock;

    private List<SpecificationItem> importedItems;
    private List<LinkedSpecificationItem> linkedItems;
    private OftRunner oftRunner;

    @BeforeEach
    void setUp()
    {
        importedItems = new ArrayList<>();
        linkedItems = new ArrayList<>();

        oftRunner = new OftRunner(serviceFactoryMock);

        lenient().when(serviceFactoryMock.createImporterService(any())).thenReturn(importerServiceMock);
        lenient().when(importerServiceMock.createImporter()).thenReturn(multiFileImporterMock);
        lenient().when(multiFileImporterMock.importAny(any())).thenReturn(multiFileImporterMock);
        lenient().when(multiFileImporterMock.getImportedItems()).thenReturn(importedItems);

        lenient().when(serviceFactoryMock.createLinker(same(importedItems))).thenReturn(linkerMock);
        lenient().when(linkerMock.link()).thenReturn(linkedItems);

        lenient().when(serviceFactoryMock.createTracer()).thenReturn(tracerMock);
        lenient().when(tracerMock.trace(same(linkedItems))).thenReturn(traceMock);

        lenient().when(serviceFactoryMock.createExporterService()).thenReturn(exporterServiceMock);

        lenient().when(serviceFactoryMock.createReportService(any())).thenReturn(reportServiceMock);
    }

    @Test
    void testImportItemsWithDefaultImportSettings()
    {
        assertThat(oftRunner.importItems(), sameInstance(importedItems));

        final ArgumentCaptor<ImportSettings> arg = ArgumentCaptor.forClass(ImportSettings.class);
        verify(serviceFactoryMock).createImporterService(arg.capture());
        assertDefaultSettings(arg.getValue());
    }

    private void assertDefaultSettings(final ImportSettings actual)
    {
        assertThat(actual.getFilters().isAnyCriteriaSet(), is(false));
        assertThat(actual.getFilters().isArtifactTypeCriteriaSet(), is(false));
        assertThat(actual.getFilters().isTagCriteriaSet(), is(false));
    }

    @Test
    void testImportItemsWithCustomImportSettings()
    {
        final ImportSettings importSettings = ImportSettings.createDefault();
        assertThat(oftRunner.importItems(importSettings), sameInstance(importedItems));
        verify(serviceFactoryMock).createImporterService(same(importSettings));
    }

    @Test
    void testLink()
    {
        assertThat(oftRunner.link(importedItems), sameInstance(linkedItems));
    }

    @Test
    void testTrace()
    {
        assertThat(oftRunner.trace(linkedItems), sameInstance(traceMock));
    }

    @Test
    void testExportToPathListOfSpecificationItemPath()
    {
        oftRunner.exportToPath(importedItems, PATH);

        final ArgumentCaptor<ExportSettings> arg = ArgumentCaptor.forClass(ExportSettings.class);
        verify(exporterServiceMock).exportToPath(any(), same(PATH), arg.capture());
        assertDefaultExportSettings(arg.getValue());
    }

    private void assertDefaultExportSettings(final ExportSettings settings)
    {
        assertThat(settings.getNewline(), equalTo(Newline.UNIX));
        assertThat(settings.getOutputFormat(), equalTo("specobject"));
    }

    @Test
    void testExportToPathListOfSpecificationItemPathExportSettings()
    {
        final ExportSettings settings = ExportSettings.createDefault();
        oftRunner.exportToPath(importedItems, PATH, settings);

        verify(exporterServiceMock).exportToPath(any(), same(PATH), same(settings));
    }

    @Test
    void testReportToStdOutTrace()
    {
        oftRunner.reportToStdOut(traceMock);
        verify(reportServiceMock).reportTraceToStdOut(same(traceMock), eq("plain"));
    }

    @Test
    void testReportToStdOutTraceReportSettings()
    {
        final ReportSettings settings = ReportSettings.builder().outputFormat("myFormat")
                .newline(Newline.OLDMAC).build();
        oftRunner.reportToStdOut(traceMock, settings);
        verify(reportServiceMock).reportTraceToStdOut(same(traceMock), eq("myFormat"));
    }

    @Test
    void testReportToPathTracePath()
    {
        oftRunner.reportToPath(traceMock, PATH);
        verify(reportServiceMock).reportTraceToPath(same(traceMock), same(PATH), eq("plain"));
    }

    @Test
    void testReportToPathTracePathReportSettings()
    {
        final ReportSettings settings = ReportSettings.builder().outputFormat("myFormat").build();
        oftRunner.reportToPath(traceMock, PATH, settings);
        verify(reportServiceMock).reportTraceToPath(same(traceMock), same(PATH), eq("myFormat"));
    }

}
