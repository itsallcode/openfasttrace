package openfasttrack.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import openfasttrack.core.Trace;

public class ReportService
{
    public void generateReport(final Trace trace, final Path outputFile,
            final ReportVerbosity verbosity)
    {
        try (OutputStream outputStream = new BufferedOutputStream(
                Files.newOutputStream(outputFile)))
        {
            new PlainTextReport(trace).renderToStreamWithVerbosityLevel(outputStream, verbosity);
        }
        catch (final IOException e)
        {
            throw new ReportException("Error generating report to output file " + outputFile, e);
        }
    }
}
