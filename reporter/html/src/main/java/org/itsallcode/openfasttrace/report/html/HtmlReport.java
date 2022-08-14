package org.itsallcode.openfasttrace.report.html;

import java.io.OutputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.report.html.view.ViewFactory;
import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;
import org.itsallcode.openfasttrace.report.html.view.html.HtmlViewFactory;

/**
 * An HTML report.
 */
public class HtmlReport implements Reportable
{
    private final Trace trace;
    private static final String REPORT_CSS_FILE = "/css/report.css";

    /**
     * Create a new instance of an {@link HtmlReport}
     * 
     * @param trace
     *            trace to be reported on
     */
    public HtmlReport(final Trace trace)
    {
        this.trace = trace;
    }

    /**
     * Get the URL to the CSS stylesheet that is used to lay out the HTML Report
     * 
     * @return the URL of the CSS stylesheet
     */
    public static URL getCssUrl()
    {
        return HtmlReport.class.getResource(REPORT_CSS_FILE);
    }

    @Override
    public void renderToStream(final OutputStream outputStream)
    {
        final ViewFactory factory = HtmlViewFactory.create(outputStream, getCssUrl());
        final ViewableContainer view = factory.createView("",
                "Specification items by artifact type");
        final ViewableContainer details = createDetails(factory);
        final ViewableContainer summary = createSummary(details, factory);
        view.add(details);
        view.add(summary);
        view.render();
    }

    private ViewableContainer createDetails(final ViewFactory factory)
    {
        final ViewableContainer details = factory.createReportDetails();
        final List<LinkedSpecificationItem> items = getSortedItems();
        addSectionedItems(factory, details, items);
        return details;
    }

    private List<LinkedSpecificationItem> getSortedItems()
    {
        final List<LinkedSpecificationItem> items = this.trace.getItems();
        items.sort(Comparator.comparing(LinkedSpecificationItem::getArtifactType)
                .thenComparing(LinkedSpecificationItem::getTitleWithFallback));
        return items;
    }

    private void addSectionedItems(final ViewFactory factory, final ViewableContainer view,
            final List<LinkedSpecificationItem> items)
    {
        String artifactType = "\0";
        ViewableContainer section = factory.createSection(artifactType, artifactType);
        for (final LinkedSpecificationItem item : items)
        {
            final String currentArtifactType = item.getArtifactType();
            if (!artifactType.equals(currentArtifactType))
            {
                artifactType = currentArtifactType;
                section = factory.createSection(artifactType, artifactType);
                view.add(section);
            }
            final Viewable itemView = factory.createSpecificationItem(item);
            section.add(itemView);
        }
    }

    private ViewableContainer createSummary(final ViewableContainer view,
            final ViewFactory factory)
    {
        final ViewableContainer summary = factory.createReportSummary();
        summary.add(factory.createTraceSummary(this.trace));
        summary.add(factory.createTableOfContents(view));
        return summary;
    }
}