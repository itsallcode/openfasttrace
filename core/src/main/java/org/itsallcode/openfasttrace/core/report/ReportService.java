package org.itsallcode.openfasttrace.core.report;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

/**
 * This service provides convenient methods to create a report for a given
 * {@link Trace}.
 */
public class ReportService
{
    private final ReporterFactoryLoader reporterFactoryLoader;

    /**
     * Create a new {@link ReportService} for the given
     * {@link ReporterFactoryLoader}
     * 
     * @param reporterFactoryLoader
     *            the {@link ReporterFactoryLoader} for the new
     *            {@link ReportService}.
     */
    public ReportService(ReporterFactoryLoader reporterFactoryLoader)
    {
        this.reporterFactoryLoader = reporterFactoryLoader;
    }

    /**
     * Generate a report for the given {@link Trace} in the given output format
     * and write it to a file.
     * 
     * @param trace
     *            the content of the report.
     * @param outputPath
     *            the path of the report to generate.
     * @param outputFormat
     *            the format of the report. Must be a value supported by
     *            {@link ReporterFactory#supportsFormat(String)}.
     */
    public void reportTraceToPath(final Trace trace, final Path outputPath,
            final String outputFormat)
    {
        try (OutputStream outputStream = Files.newOutputStream(outputPath))
        {
            reportTraceToStream(trace, outputStream, outputFormat);
        }
        catch (final IOException e)
        {
            throw new ReportException("Error generating stream to output path " + outputPath, e);
        }
    }

    /**
     * Generate a report for the given {@link Trace} in the given output format
     * and write it to standard out.
     * 
     * @param trace
     *            the content of the report.
     * @param outputFormat
     *            the format of the report. Must be a value supported by
     *            {@link ReporterFactory#supportsFormat(String)}.
     */
    public void reportTraceToStdOut(final Trace trace, final String outputFormat)
    {
        reportTraceToStream(trace, getStdOutStream(), outputFormat);
    }

    // Using System.out by intention
    @SuppressWarnings("squid:S106")
    private PrintStream getStdOutStream()
    {
        return System.out;
    }

    private void reportTraceToStream(final Trace trace, final OutputStream outputStream,
            final String outputFormat)
    {
        final Reportable report = createReport(trace, outputFormat);
        report.renderToStream(outputStream);
        try
        {
            outputStream.flush();
        }
        catch (final IOException exception)
        {
            throw new ReportException(exception.getMessage());
        }
    }

    private Reportable createReport(final Trace trace, final String outputFormat)
    {
        final ReporterFactory reporterFactory = reporterFactoryLoader
                .getReporterFactory(outputFormat);
        return reporterFactory.createImporter(trace);
    }
}
