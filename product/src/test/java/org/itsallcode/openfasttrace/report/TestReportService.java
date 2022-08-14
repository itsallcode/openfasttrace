package org.itsallcode.openfasttrace.report;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard.SysOut;
import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.itsallcode.openfasttrace.api.report.ReporterContext;
import org.itsallcode.openfasttrace.core.report.ReportService;
import org.itsallcode.openfasttrace.core.report.ReporterFactoryLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SystemOutGuard.class)
class TestReportService
{
    @Mock
    Trace traceMock;

    @Test
    void testReportPlainText(@SysOut final Capturable out)
    {
        final ReportSettings settings = ReportSettings //
                .builder() //
                .verbosity(ReportVerbosity.MINIMAL) //
                .build();
        out.capture();
        createService(settings).reportTraceToStdOut(this.traceMock, settings.getOutputFormat());
        assertThat(out.getCapturedData(), equalTo("not ok\n"));
    }

    @Test
    void testReportHtml(@SysOut final Capturable out)
    {
        final ReportSettings settings = ReportSettings //
                .builder() //
                .outputFormat("html") //
                .build();
        out.capture();
        createService(settings).reportTraceToStdOut(this.traceMock, settings.getOutputFormat());
        assertThat(out.getCapturedData(), startsWith("<!DOCTYPE html>"));
    }

    @Test
    void testInvalidReportFormatThrowsIllegalArgumentException()
    {
        final ReportSettings settings = ReportSettings //
                .builder() //
                .verbosity(ReportVerbosity.QUIET) //
                .build();
        final ReportService service = createService(settings);
        final ExporterException exception = assertThrows(ExporterException.class,
                () -> service.reportTraceToStdOut(this.traceMock, "invalid"));
        assertThat(exception.getMessage(), equalTo("Found no matching reporter for output format 'invalid'"));
    }

    @Test
    void testReportToIllegalPathThrowsReportExpection(@TempDir final Path tempDir)
            throws IOException
    {
        final Path readOnlyFilePath = createReadOnlyFile(tempDir);
        makeFileReadOnly(readOnlyFilePath);
        try
        {
            final ReportSettings settings = ReportSettings //
                    .builder() //
                    .verbosity(ReportVerbosity.QUIET) //
                    .build();
            final ReportService service = createService(settings);
            final String outputFormat = settings.getOutputFormat();
            final ReportException exception = assertThrows(ReportException.class,
                    () -> service.reportTraceToPath(this.traceMock,
                            readOnlyFilePath, outputFormat));
            assertThat(exception.getMessage(), equalTo("Error generating stream to output path " + readOnlyFilePath));
        }
        finally
        {
            makeFileWritable(readOnlyFilePath);
        }
    }

    private ReportService createService(final ReportSettings settings)
    {
        return new ReportService(new ReporterFactoryLoader(new ReporterContext(settings)));
    }

    private Path createReadOnlyFile(final Path tempDir) throws IOException
    {
        final Path readOnlyFilePath = tempDir.resolve("readonly.txt");
        Files.write(readOnlyFilePath, "r/o".getBytes());
        return readOnlyFilePath;
    }

    private void makeFileReadOnly(final Path readOnlyFilePath) throws IOException
    {
        readOnlyFilePath.toFile().setReadOnly();
    }

    private void makeFileWritable(final Path readOnlyFilePath)
    {
        readOnlyFilePath.toFile().setWritable(true);
    }
}