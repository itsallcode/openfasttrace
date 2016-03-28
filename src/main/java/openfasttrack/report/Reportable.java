package openfasttrack.report;

import java.io.OutputStream;

/**
 * Interface for coverage reports.
 */
public interface Reportable
{
    /**
     * Render the plain text coverage report.
     *
     * @param outputStream
     *            the output stream to which the report is rendered.
     * @param verbosity
     *            the level of detail that is reported.
     */
    void renderToStreamWithVerbosityLevel(OutputStream outputStream, ReportVerbosity verbosity);
}