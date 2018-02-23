package org.itsallcode.openfasttrace.report;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.core.*;

/**
 * Renders a coverage report in plain text. This is intended for command line
 * application output.
 */
public class PlainTextReport implements Reportable
{
    private final Trace trace;
    private static final Comparator<LinkedSpecificationItem> LINKED_ITEM_BY_ID = Comparator
            .comparing(LinkedSpecificationItem::getId);
    private final Newline newline;
    private int nonEmptySections = 0;

    /**
     * Create a new instance of {@link PlainTextReport}
     *
     * @param trace
     *            the trace that will be reported.
     * @param newline
     *            the newline format
     */
    // [impl->dsn~newline-format~1]
    public PlainTextReport(final Trace trace, final Newline newline)
    {
        this.trace = trace;
        this.newline = newline;
    }

    @Override
    public void renderToStreamWithVerbosityLevel(final OutputStream outputStream,
            final ReportVerbosity verbosity)
    {
        final Charset charset = StandardCharsets.UTF_8;
        try (final PrintStream report = new PrintStream(outputStream, false, charset.displayName()))
        {
            renderToPrintStreamWithVerbosityLevel(report, verbosity);
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new ReportException("Encoding charset '" + charset + "' not supported", e);
        }
    }

    private void renderToPrintStreamWithVerbosityLevel(final PrintStream report,
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
        case FAILURE_SUMMARIES:
            renderFailureSummaries(report);
            separateItemsFromSummary(report);
            renderSummary(report);
            break;
        case FAILURE_DETAILS:
            renderFailureDetails(report);
            separateItemsFromSummary(report);
            renderSummary(report);
            break;
        case ALL:
            renderAll(report);
            report.print(this.newline);
            renderSummary(report);
            break;
        default:
            throw new IllegalStateException(
                    "Unable to create report for unknown verbosity level " + verbosity);
        }
    }

    private void separateItemsFromSummary(final PrintStream report)
    {
        if (this.trace.countDefects() > 0)
        {
            report.print(this.newline);
        }
    }

    private void renderResultStatus(final PrintStream report)
    {
        report.print(translateStatus(this.trace.hasNoDefects()));
        report.print(this.newline.toString());
    }

    private String translateStatus(final boolean ok)
    {
        return ok ? "ok" : "not ok";
    }

    // [impl->dsn~reporting.plain-text.summary~2]
    private void renderSummary(final PrintStream report)
    {
        report.print(translateStatus(this.trace.hasNoDefects()));
        report.print(" - ");
        report.print(this.trace.count());
        report.print(" total");
        if (this.trace.countDefects() != 0)
        {
            report.print(", ");
            report.print(this.trace.countDefects());
            report.print(" defect");
        }
        report.print(this.newline);
    }

    private void renderFailureIds(final PrintStream report)
    {
        this.trace.getDefectIds().stream() //
                .sorted()//
                .forEachOrdered(id -> {
                    report.print(id);
                    report.print(this.newline.toString());
                });
    }

    private void renderFailureSummaries(final PrintStream report)
    {
        this.trace.getDefectItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemSummary(report, item));
    }

    // [impl->dsn~reporting.plain-text.specification-item-overview~2]
    private void renderItemSummary(final PrintStream report, final LinkedSpecificationItem item)
    {
        report.print(translateStatus(!item.isDefect()));
        report.print(" - ");
        renderItemLinkCounts(report, item);
        report.print(" - ");
        report.print(item.getId().toString());
        report.print(" ");
        renderMaturity(report, item);
        report.print(translateArtifactTypeCoverage(item));
        report.print(this.newline);
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

    private void renderItemLinkCounts(final PrintStream report, final LinkedSpecificationItem item)
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

    private void renderMaturity(final PrintStream report, final LinkedSpecificationItem item)
    {
        final ItemStatus status = item.getStatus();
        if (status != ItemStatus.APPROVED)
        {
            report.print("[");
            report.print(status);
            report.print("] ");
        }
    }

    private void renderFailureDetails(final PrintStream report)
    {
        this.trace.getDefectItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemDetails(report, item));
    }

    private void renderAll(final PrintStream report)
    {
        this.trace.getItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemDetails(report, item));
    }

    private void renderItemDetails(final PrintStream report, final LinkedSpecificationItem item)
    {
        renderItemSummary(report, item);
        renderDescription(report, item);
        renderLinks(report, item);
        renderTags(report, item);
        renderItemDetailsEnd(report);
    }

    private void renderEmptyItemDetailsLine(final PrintStream report)
    {
        report.print("|");
        report.print(this.newline);
    }

    private void renderDescription(final PrintStream report, final LinkedSpecificationItem item)
    {
        final String description = item.getDescription();
        if (description != null && !description.isEmpty())
        {
            renderEmptyItemDetailsLine(report);
            for (final String line : description.split(Newline.anyNewlineReqEx()))
            {
                report.print("| ");
                report.print(line);
                report.print(this.newline);
            }
            ++this.nonEmptySections;
        }
    }

    // [impl->dsn~reporting.plain-text.link-details~1]
    private void renderLinks(final PrintStream report, final LinkedSpecificationItem item)
    {

        final Map<LinkStatus, List<LinkedSpecificationItem>> links = item.getLinks();
        if (!links.isEmpty())
        {
            renderEmptyItemDetailsLine(report);
            renderOrderedLinks(report, links);
            ++this.nonEmptySections;
        }
    }

    private void renderOrderedLinks(final PrintStream report,
            final Map<LinkStatus, List<LinkedSpecificationItem>> links)
    {
        final List<TracedLink> tracedLinks = flattenTracedLinks(links);
        tracedLinks.stream() //
                .sorted((a, b) -> a.getOtherLinkEnd().getId()
                        .compareTo(b.getOtherLinkEnd().getId())) //
                .forEachOrdered(link -> renderLink(report, link));
    }

    private List<TracedLink> flattenTracedLinks(
            final Map<LinkStatus, List<LinkedSpecificationItem>> links)
    {
        final List<TracedLink> tracedLinks = new ArrayList<>();
        for (final Entry<LinkStatus, List<LinkedSpecificationItem>> entry : links.entrySet())
        {
            for (final LinkedSpecificationItem other : entry.getValue())
            {
                tracedLinks.add(new TracedLink(other, entry.getKey()));
            }
        }
        return tracedLinks;
    }

    private void renderLink(final PrintStream report, final TracedLink link)
    {
        final LinkStatus status = link.getStatus();
        report.print(status.isIncoming() ? "|<-- (" : "|--> (");
        report.print(status.getShortTag());
        report.print(") ");
        report.print(link.getOtherLinkEnd().getId());
        report.print(this.newline);
    }

    private void renderTags(final PrintStream report, final LinkedSpecificationItem item)
    {
        final List<String> tags = item.getTags();
        if (tags != null && !tags.equals(Collections.emptyList()))
        {
            renderEmptyItemDetailsLine(report);
            report.print("| #: ");
            report.print(tags.stream().collect(Collectors.joining(", ")));
            report.print(this.newline);
            ++this.nonEmptySections;
        }
    }

    private void renderItemDetailsEnd(final PrintStream report)
    {
        if (this.nonEmptySections > 0)
        {
            renderEmptyItemDetailsLine(report);
        }
    }
}