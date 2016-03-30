package openfasttrack.report;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.io.OutputStream;
import java.io.PrintStream;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Trace;

/**
 * Renders a coverage report in plain text. This is intended for command line
 * application output.
 */
public class PlainTextReport implements Reportable
{

    private final Trace trace;

    /**
     * Create a new instance of {@link PlainTextReport}
     *
     * @param trace
     *            the trace that will be reported.
     */
    public PlainTextReport(final Trace trace)
    {
        this.trace = trace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * openfasttrack.report.Reportable#renderToStreamWithVerbosityLevel(java.io.
     * OutputStream, openfasttrack.report.ReportVerbosity)
     */
    @Override
    public void renderToStreamWithVerbosityLevel(final OutputStream outputStream,
            final ReportVerbosity verbosity)
    {
        final PrintStream report = new PrintStream(outputStream);
        switch (verbosity)
        {
        case QUIET:
            break;
        case MINIMAL:
            renderResultStatus(report);
            break;
        case SUMMARY:
            renderSummary(report);
            break;
        case FAILURES:
            renderFailureIds(report);
            break;
        case FAILURE_DETAILS:
            renderFailureDetails(report);
            report.println();
            renderSummary(report);
            break;
        default:
            throw new IllegalStateException(
                    "Unable to create report for unknown verbosity level " + verbosity);
        }
    }

    private void renderResultStatus(final PrintStream report)
    {
        report.println(translateStatus(this.trace.isAllCovered()));
    }

    private String translateStatus(final boolean ok)
    {
        return ok ? "ok" : "not ok";
    }

    private void renderSummary(final PrintStream report)
    {
        report.print(translateStatus(this.trace.isAllCovered()));
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

    private void renderFailureDetails(final PrintStream report)
    {
        this.trace.getUncoveredItems().stream() //
                .sorted((item, other) -> item.getId().compareTo(other.getId())) //
                .forEachOrdered(item -> reportItemDetails(item, report));
    }

    private void reportItemDetails(final LinkedSpecificationItem item, final PrintStream report)
    {
        report.print(translateStatus(item.isOk()));
        report.print(" - ");
        report.println(item.getId().toString());
        for (final String line : item.getDescription().split("\n"))
        {
            report.print("# ");
            report.println(line);
        }
    }
}
