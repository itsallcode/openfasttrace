package org.itsallcode.openfasttrace.report.plaintext;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.api.report.ReportException;
import org.itsallcode.openfasttrace.api.report.Reportable;

/**
 * Renders a coverage stream in plain text. This is intended for command line
 * application output.
 */
public class PlainTextReport implements Reportable
{
    private final Trace trace;
    private static final Comparator<LinkedSpecificationItem> LINKED_ITEM_BY_ID = Comparator
            .comparing(LinkedSpecificationItem::getId);
    private int nonEmptySections = 0;
    private final ReportSettings settings;
    private final TextFormatter formatter;

    /**
     * Create a new instance of {@link PlainTextReport}
     *
     * @param trace
     *            the trace that will be reported.
     * @param settings
     *            report settings
     */
    // [impl->dsn~newline-format~1]
    public PlainTextReport(final Trace trace, final ReportSettings settings)
    {
        this.trace = trace;
        this.settings = settings;
        this.formatter = TextFormatterFactory.createFormatter(settings.getColorScheme());
    }

    @Override
    public void renderToStream(final OutputStream outputStream)
    {
        final Charset charset = StandardCharsets.UTF_8;
        try (final PrintStream report = new PrintStream(outputStream, false, charset.displayName()))
        {
            renderToPrintStream(report);
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new ReportException("Encoding charset '" + charset + "' not supported", e);
        }
    }

    private void renderToPrintStream(final PrintStream report)
    {
        switch (this.settings.getReportVerbosity())
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
            renderFailureDetails(report, this.settings.showOrigin());
            separateItemsFromSummary(report);
            renderSummary(report);
            break;
        case ALL:
            renderAll(report, this.settings.showOrigin());
            report.print(this.settings.getNewline());
            renderSummary(report);
            break;
        default:
            throw new IllegalStateException("Unable to create stream for unknown verbosity level "
                    + this.settings.getReportVerbosity());
        }
    }

    private void separateItemsFromSummary(final PrintStream report)
    {
        if (this.trace.countDefects() > 0)
        {
            report.print(this.settings.getNewline());
        }
    }

    private void renderResultStatus(final PrintStream report)
    {
        report.print(translateStatus(this.trace.hasNoDefects()));
        report.print(this.settings.getNewline().toString());
    }

    private String translateStatus(final boolean ok)
    {
        return ok ? this.formatter.formatOk("ok") : this.formatter.formatNotOk("not ok");
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
        report.print(this.settings.getNewline());
    }

    private void renderFailureIds(final PrintStream report)
    {
        this.trace.getDefectIds().stream() //
                .sorted()//
                .forEachOrdered(id -> {
                    report.print(id);
                    report.print(this.settings.getNewline().toString());
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
        report.print(this.formatter.formatStrong(item.getId().toString()));
        report.print(" ");
        renderMaturity(report, item);
        report.print(translateArtifactTypeCoverage(item));
        report.print(this.settings.getNewline());
    }

    private String translateArtifactTypeCoverage(final LinkedSpecificationItem item)
    {
        final Comparator<String> byTypeName = Comparator.comparing(a -> a.replaceFirst("[-+]", ""));

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

    private void renderFailureDetails(final PrintStream report, final boolean showOrigin)
    {
        this.trace.getDefectItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemDetails(report, item, showOrigin));
    }

    private void renderAll(final PrintStream report, final boolean showOrigin)
    {
        this.trace.getItems().stream() //
                .sorted(LINKED_ITEM_BY_ID) //
                .forEachOrdered(item -> renderItemDetails(report, item, showOrigin));
    }

    private void renderItemDetails(final PrintStream report, final LinkedSpecificationItem item,
            final boolean showOrigin)
    {
        renderItemSummary(report, item);
        renderDescription(report, item);
        if (showOrigin)
        {
            renderOrigin(report, item);
        }
        renderLinks(report, item, showOrigin);
        renderTags(report, item);
        renderItemDetailsEnd(report);
    }

    private void renderOrigin(final PrintStream report, final Location location)
    {
        report.print("(");
        report.print(location.getPath());
        report.print(":");
        report.print(location.getLine());
        report.print(")");
    }

    private void renderEmptyItemDetailsLine(final PrintStream report)
    {
        report.print("|");
        report.print(this.settings.getNewline());
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
                report.print(this.settings.getNewline());
            }
            ++this.nonEmptySections;
        }
    }

    // [impl->dsn~reporting.plain-text.link-details~1]
    private void renderLinks(final PrintStream report, final LinkedSpecificationItem item,
            final boolean showOrigin)
    {
        if (item.hasLinks())
        {
            renderEmptyItemDetailsLine(report);
            renderOrderedLinks(report, item, showOrigin);
            ++this.nonEmptySections;
        }
    }

    private void renderOrderedLinks(final PrintStream report, final LinkedSpecificationItem item,
            final boolean showOrigin)
    {
        item.getTracedLinks() //
                .stream() //
                .sorted(Comparator.comparing(a -> a.getOtherLinkEnd().getId())) //
                .forEachOrdered(link -> renderLink(report, link, showOrigin));
    }

    private void renderLink(final PrintStream report, final TracedLink link,
            final boolean showOrigin)
    {
        final LinkStatus status = link.getStatus();
        report.print(status.isIncoming() ? "|<-- (" : "|--> (");
        report.print(status.getShortTag());
        report.print(") ");
        report.print(link.getOtherLinkEnd().getId());
        report.print(this.settings.getNewline());
        if (showOrigin)
        {
            final Location location = link.getOtherLinkEnd().getLocation();
            if (location != null)
            {
                report.print("|        ");
                renderOrigin(report, location);
                report.print(this.settings.getNewline());
            }
        }
    }

    private void renderTags(final PrintStream report, final LinkedSpecificationItem item)
    {
        final List<String> tags = item.getTags();
        if (tags != null && !tags.equals(Collections.emptyList()))
        {
            renderEmptyItemDetailsLine(report);
            report.print("| #: ");
            report.print(tags.stream().collect(Collectors.joining(", ")));
            report.print(this.settings.getNewline());
            ++this.nonEmptySections;
        }
    }

    private void renderOrigin(final PrintStream report, final LinkedSpecificationItem item)
    {
        final Location location = item.getLocation();
        if (location != null)
        {
            renderEmptyItemDetailsLine(report);
            report.print("| (");
            report.print(location.getPath());
            report.print(":");
            report.print(location.getLine());
            report.print(")");
            report.print(this.settings.getNewline());
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