package org.itsallcode.openfasttrace.report;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.itsallcode.openfasttrace.core.Trace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@ExtendWith(TempDirectory.class)
class TestReportService
{
    private static final String OUTPUT_FORMAT = "format";

    @Mock
    private ReporterFactoryLoader reporterFactoryLoaderMock;
    @Mock
    private Trace traceMock;
    @Mock
    private ReporterFactory reporterFactoryMock;
    @Mock
    private Reportable reportableMock;

    private ReportService service;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.initMocks(this);
        service = new ReportService(reporterFactoryLoaderMock);
        when(reporterFactoryLoaderMock.getReporterFactory(OUTPUT_FORMAT))
                .thenReturn(reporterFactoryMock);
        when(reporterFactoryMock.createImporter(same(traceMock))).thenReturn(reportableMock);
    }

    @Test
    void testReportTraceToPath(@TempDir Path tempDir)
    {
        service.reportTraceToPath(traceMock, tempDir.resolve("output"), OUTPUT_FORMAT);
        verify(reportableMock).renderToStream(any());
    }

    @Test
    void testReportTraceToStdOut()
    {
        service.reportTraceToStdOut(traceMock, OUTPUT_FORMAT);
        verify(reportableMock).renderToStream(any());
    }
}
