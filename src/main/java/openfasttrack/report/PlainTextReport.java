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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Trace;

/**
 * Renders a coverage report in plain text. This is intended for command line
 * application output.
 */
public class PlainTextReport implements Reportable
{

    private static final String LINE_ENDING = "\\r\\n|\\r|\\n";
    private final Trace trace;
    private static final Comparator<? super LinkedSpecificationItem> LINKED_ITEM_BY_ID = (item,
            other) -> item.getId().compareTo(other.getId());

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

    @Override
    public void renderToStreamWithVerbosityLevel(final OutputStream outputStream,
            final ReportVerbosity verbosity)
    {
        try (final PrintStream report = createPrintStream(outputStream, StandardCharsets.UTF_8))
        {
            renderToStreamWithVerbosityLevel(report, verbosity);
        }
    }

    private void renderToStreamWithVerbosityLevel(final PrintStream report,
            final ReportVerbosity verbosity)
    {
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
        case ALL:
            renderAll(report);
            report.println();
            renderSummary(report);
            break;
        default:
            throw new IllegalStateException(
                    "Unable to create report for unknown verbosity level " + verbosity);
        }
    }

    private static PrintStream createPrintStream(final OutputStream outputStream,
            final Charset charset)
    {
        try
        {
            return new PrintStream(outputStream, false, charset.displayName());
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new ReportException("Encoding charset '" + charset + "' not supported", e);
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
                .forEachOrdered(report::println);
    }

    private void renderFailureDetails(final PrintStream report)
    {
        this.trace.getUncoveredItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemSummary(item, report));
    }

    private void renderItemSummary(final LinkedSpecificationItem item, final PrintStream report)
    {
        report.print(translateStatus(!item.isDefect()));
        report.print(" - ");
        renderItemLinkCounts(item, report);
        report.print(" - ");
        report.print(item.getId().toString());
        report.print(" ");
        report.println(translateArtifactTypeCoverage(item));
    }

    private String translateArtifactTypeCoverage(final LinkedSpecificationItem item)
    {
        final Comparator<String> byTypeName = (a, b) -> a.replaceFirst("[-+]", "")
                .compareTo(b.replaceFirst("[-+]", ""));

        final Stream<String> uncoveredStream = item.getUncoveredArtifactTypes().stream()
                .map(x -> "-" + x);
        return "(" + Stream.concat( //
                Stream.concat( //
                        uncoveredStream, //
                        item.getCoveredArtifactTypes().stream() //
                ), //
                item.getOverCoveredArtifactTypes().stream().map(x -> "+" + x) //
        ) //
                .sorted(byTypeName) //
                .collect(Collectors.joining(", ")) + ")";
    }

    private void renderItemLinkCounts(final LinkedSpecificationItem item, final PrintStream report)
    {
        report.print(item.countIncomingBadLinks());
        report.print("/");
        report.print(item.countIncomingLinks());
        report.print(">");
        report.print(item.countDuplicateLinks());
        report.print(">");
        report.print(item.countOutgoingBadLinks());
        report.print("/");
        report.print(item.countOutgoingLinks());
    }

    private void renderAll(final PrintStream report)
    {
        this.trace.getItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemDetails(item, report));
    }

    private void renderItemDetails(final LinkedSpecificationItem item, final PrintStream report)
    {
        renderItemSummary(item, report);
        report.println("#");
    
        for (final String line : item.getDescription().split(LINE_ENDING))
        {
            report.print("# ");
            report.println(line);
        }
        report.println("#");
    }
}