package org.itsallcode.openfasttrace.core;

/*-
 * #%L
 * OpenFastTrace Core
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.api.importer.ImporterService;
import org.itsallcode.openfasttrace.api.importer.MultiFileImporter;
import org.itsallcode.openfasttrace.core.exporter.ExporterService;
import org.itsallcode.openfasttrace.core.report.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

        when(serviceFactoryMock.createImporterService(any())).thenReturn(importerServiceMock);
        when(importerServiceMock.createImporter()).thenReturn(multiFileImporterMock);
        when(multiFileImporterMock.importAny(any())).thenReturn(multiFileImporterMock);
        when(multiFileImporterMock.getImportedItems()).thenReturn(importedItems);

        when(serviceFactoryMock.createLinker(same(importedItems))).thenReturn(linkerMock);
        when(linkerMock.link()).thenReturn(linkedItems);

        when(serviceFactoryMock.createTracer()).thenReturn(tracerMock);
        when(tracerMock.trace(same(linkedItems))).thenReturn(traceMock);

        when(serviceFactoryMock.createExporterService()).thenReturn(exporterServiceMock);

        when(serviceFactoryMock.createReportService(any())).thenReturn(reportServiceMock);
    }

    @Test
    void testImportItemsWithDefaultImportSettings()
    {
        assertThat(oftRunner.importItems(), sameInstance(importedItems));

        final ArgumentCaptor<ImportSettings> arg = ArgumentCaptor.forClass(ImportSettings.class);
        verify(serviceFactoryMock).createImporterService(arg.capture());
        assertDefaultSettings(arg.getValue());
    }

    private void assertDefaultSettings(ImportSettings actual)
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

    private void assertDefaultExportSettings(ExportSettings settings)
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
