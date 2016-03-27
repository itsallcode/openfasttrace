package openfasttrack.report;

import java.io.OutputStream;
import java.io.PrintStream;

import openfasttrack.core.Trace;

public class PlainTextReport
{

    private final Trace trace;

    public PlainTextReport(final Trace trace)
    {
        this.trace = trace;
    }

    public void renderToStreamWithVerbosityLevel(final OutputStream outputStream,
            final ReportVerbosity verbosity)
    {
        final PrintStream report = new PrintStream(outputStream);
        switch (verbosity)
        {
        case MINIMAL:
            renderResultStatus(report);
            break;
        case SUMMARY:
            renderSummary(report);
            break;
        case FAILURES:
            renderFailureIds(report);
            break;
        default:
            throw new IllegalStateException(
                    "Unable to create report for unknown verbosity level " + verbosity);
        }
    }

    private void renderResultStatus(final PrintStream report)
    {
        report.println(getResultStatus());
    }

    private String getResultStatus()
    {
        return this.trace.isAllCovered() ? "OK" : "Not OK";
    }

    private void renderSummary(final PrintStream report)
    {
        report.print(getResultStatus());
        report.print(" - ");
        report.print(this.trace.count());
        report.print(" total");
        if (this.trace.countUncovered() != 0)
        {
            report.print(", ");
            report.print(this.trace.countUncovered());
            report.print(" not covered");
        }
        report.println();
    }

    private void renderFailureIds(final PrintStream report)
    {
        this.trace.getUncoveredIds().stream() //
                .sorted((id, other) -> id.compareTo(other)) //
                .forEachOrdered(id -> report.println(id));
    }
}
