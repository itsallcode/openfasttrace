package org.itsallcode.openfasttrace.report.html.view.html;

/*-
 * #%L
 * OpenFastTrace HTML Reporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import static org.itsallcode.openfasttrace.report.html.view.html.CharacterConstants.CHECK_MARK;
import static org.itsallcode.openfasttrace.report.html.view.html.CharacterConstants.CROSS_MARK;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.*;
import org.itsallcode.openfasttrace.report.html.view.IndentationHelper;
import org.itsallcode.openfasttrace.report.html.view.Viewable;

class HtmlSpecificationItem implements Viewable
{

    private final LinkedSpecificationItem item;
    private final PrintStream stream;
    private final MarkdownConverter converter = new MarkdownConverter();

    HtmlSpecificationItem(final PrintStream stream, final LinkedSpecificationItem item)
    {
        this.stream = stream;
        this.item = item;
    }

    @Override
    public void render(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        final SpecificationItemId id = this.item.getId();
        renderStart(indentation, id);
        renderSummary(indentation, id);
        renderId(indentation, id);
        renderDescription(indentation);
        renderRationale(indentation);
        renderComment(indentation);
        renderNeeds(indentation);
        renderOrigin(indentation);
        renderLinks(indentation);
        renderEnd(indentation);
    }

    protected void renderStart(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("<section class=\"sitem\" id=\"");
        this.stream.print(id);
        this.stream.println("\">");
        this.stream.print(indentation);
        this.stream.println("  <details>");
    }

    private void renderId(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("    <p class=\"id\">");
        this.stream.print(id);
        this.stream.println("</p>");
    }

    protected void renderSummary(final String indentation, final SpecificationItemId id)
    {
        this.stream.print(indentation);
        this.stream.print("    <summary title=\"");
        this.stream.print(id);
        this.stream.print("\">");
        this.stream.print(this.item.isDefect() ? CROSS_MARK : CHECK_MARK);
        this.stream.print(" <b>");
        this.stream.print(this.item.getTitleWithFallback());
        this.stream.print("</b><small>, rev. ");
        this.stream.print(id.getRevision());
        this.stream.print(", ");
        this.stream.print(id.getArtifactType());
        this.stream.println("</small></summary>");
    }

    protected void renderDescription(final String indentation)
    {
        final String description = this.item.getDescription();
        if (description != null && !description.isEmpty())
        {
            renderMultilineText(indentation, description);
        }
    }

    protected void renderMultilineText(final String indentation, final String text)
    {
        this.stream.print(indentation);
        this.stream.print("    ");
        this.stream.println(this.converter.convert(text));
    }

    private void renderRationale(final String indentation)
    {
        final String rationale = this.item.getItem().getRationale();
        if (rationale != null && !rationale.isEmpty())
        {
            this.stream.print(indentation);
            this.stream.println("    <h6>Rationale:</h6>");
            renderMultilineText(indentation, rationale);
        }
    }

    private void renderComment(final String indentation)
    {
        final String comment = this.item.getItem().getComment();
        if (comment != null && !comment.isEmpty())
        {
            this.stream.print(indentation);
            this.stream.println("    <h6>Comment:</h6>");
            renderMultilineText(indentation, comment);
        }
    }

    private void renderNeeds(final String indentation)
    {
        if ((this.item.getNeedsArtifactTypes() != null
                && !this.item.getNeedsArtifactTypes().isEmpty())
                || (this.item.getUncoveredArtifactTypes() != null
                        && !this.item.getUncoveredArtifactTypes().isEmpty())
                || (this.item.getOverCoveredArtifactTypes() != null
                        && !this.item.getOverCoveredArtifactTypes().isEmpty()))
        {
            this.stream.print(indentation);
            this.stream.print("    <h6>Needs: ");
            this.stream.print(translateArtifactTypeCoverage(this.item));
            this.stream.println("</h6>");
        }
    }

    private String translateArtifactTypeCoverage(final LinkedSpecificationItem item)
    {
        final Comparator<String> byTypeName = Comparator.comparing(a -> a.replaceFirst("<(?:ins|del)>", ""));

        final Stream<String> uncoveredStream = item.getUncoveredArtifactTypes().stream()
                .map(x -> "<ins>" + x + "</ins>");
        return Stream.concat( //
                Stream.concat( //
                        uncoveredStream, //
                        item.getCoveredArtifactTypes().stream() //
                ), //
                item.getOverCoveredArtifactTypes().stream().map(x -> "<del>" + x + "</del>") //
        ) //
                .sorted(byTypeName) //
                .collect(Collectors.joining(", "));
    }

    private void renderOrigin(final String indentation)
    {
        final String origin = OriginLinkFormatter.formatAsBlock(this.item.getLocation());
        if (!origin.isEmpty())
        {
            this.stream.print(indentation);
            this.stream.print("    ");
            this.stream.println(origin);
        }
    }

    private void renderLinks(final String indentation)
    {
        renderLinkForDirection(indentation, true);
        renderLinkForDirection(indentation, false);
    }

    protected void renderLinkForDirection(final String indentation, final boolean in)
    {
        final int total = in ? this.item.countIncomingLinks() : this.item.countOutgoingLinks();
        if (total > 0)
        {
            this.stream.print(indentation);
            this.stream.print("    <div class=\"");
            this.stream.print(in ? "in" : "out");
            this.stream.println("\">");
            this.stream.print(indentation);
            this.stream.print("      <h6>");
            this.stream.print(in ? "In" : "Out");
            this.stream.print(": ");
            this.stream.print(total);
            this.stream.println("</h6>");
            this.stream.print(indentation);
            this.stream.println("      <ul>");
            final Stream<TracedLink> links = this.item.getTracedLinks().stream()
                    .filter(in ? TracedLink::isIncoming : TracedLink::isOutgoing);
            final List<TracedLink> sortedLinks = sortLinkStreamById(links);
            renderLinkEntry(sortedLinks, indentation);
            this.stream.print(indentation);
            this.stream.println("      </ul>");
            this.stream.print(indentation);
            this.stream.println("    </div>");
        }
    }

    protected List<TracedLink> sortLinkStreamById(final Stream<TracedLink> tracedLinkStream)
    {
        return tracedLinkStream //
                .sorted(Comparator.comparing(a -> a.getOtherLinkEnd().getId().toString())) //
                .collect(Collectors.toList());
    }

    protected void renderLinkEntry(final List<TracedLink> outLinks, final String indentation)
    {
        for (final TracedLink link : outLinks)
        {
            final SpecificationItemId otherId = link.getOtherLinkEnd().getId();
            this.stream.print(indentation);
            this.stream.print("        <li><a href=\"#");
            this.stream.print(otherId);
            this.stream.print("\">");
            this.stream.print(otherId);
            this.stream.print("</a>");
            if (link.getStatus() != LinkStatus.COVERS
                    && link.getStatus() != LinkStatus.COVERED_SHALLOW)
            {
                this.stream.print(" <em>(" + link.getStatus() + ")</em>");
            }
            renderLinkOrigin(link);
            this.stream.println("</li>");
        }
    }

    private void renderLinkOrigin(final TracedLink link)
    {
        final String origin = OriginLinkFormatter
                .formatAsSpan(link.getOtherLinkEnd().getLocation());
        if (!origin.isEmpty())
        {
            this.stream.print(" ");
            this.stream.print(origin);
        }
    }

    protected void renderEnd(final String indentation)
    {
        this.stream.print(indentation);
        this.stream.println("  </details>");
        this.stream.print(indentation);
        this.stream.println("</section>");
    }
}
