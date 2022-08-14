package org.itsallcode.openfasttrace.core.report;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
