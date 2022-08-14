package org.itsallcode.openfasttrace.report.html.view;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;

/**
 * Interface for abstract factory that creates OFT view (e.g. for reports).
 */
public interface ViewFactory
{
    /**
     * Create a view element that represents a {@link LinkedSpecificationItem}.
     * 
     * @param item
     *            linked specification item
     * @return view representing the linked specification item
     */
    Viewable createSpecificationItem(final LinkedSpecificationItem item);

    /**
     * Create a section.
     * 
     * @param id
     *            section ID
     * @param title
     *            section title
     * @return section
     */
    ViewableContainer createSection(final String id, final String title);

    /**
     * Create a view.
     * 
     * @param id
     *            ID of the specification item
     * @param title
     *            title of the specification item
     * @return view element
     */
    ViewableContainer createView(final String id, final String title);

    /**
     * Create a trace summary.
     * 
     * @param trace
     *            trace from which the summary is created
     * 
     * @return summary view for tracing results
     */
    Viewable createTraceSummary(final Trace trace);

    /**
     * Create a container for the details of the report
     * 
     * @return report details container
     */
    ViewableContainer createReportDetails();

    /**
     * Create a report summary.
     * 
     * @return summary view for tracing results
     */
    ViewableContainer createReportSummary();

    /**
     * Create a table of contents.
     * 
     * @param from
     *            view container from which the ToC is taken
     * 
     * @return table of contents
     */
    Viewable createTableOfContents(final ViewableContainer from);
}
