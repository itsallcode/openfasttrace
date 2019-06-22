package org.itsallcode.openfasttrace.report.html.view;

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
    public Viewable createSpecificationItem(final LinkedSpecificationItem item);

    /**
     * Create a section.
     * 
     * @param id
     *            section ID
     * @param title
     *            section title
     * @return section
     */
    public ViewableContainer createSection(final String id, final String title);

    /**
     * Create a view.
     * 
     * @param id
     *            ID of the specification item
     * @param title
     *            title of the specification item
     * @return view element
     */
    public ViewableContainer createView(final String id, final String title);

    /**
     * Create a trace summary.
     * 
     * @param trace
     *            trace from which the summary is created
     * 
     * @return summary view for tracing results
     */
    public Viewable createTraceSummary(final Trace trace);

    /**
     * Create a container for the details of the report
     * 
     * @return report details container
     */
    public ViewableContainer createReportDetails();

    /**
     * Create a report summary.
     * 
     * @return summary view for tracing results
     */
    public ViewableContainer createReportSummary();

    /**
     * Create a table of contents.
     * 
     * @param from
     *            view container from which the ToC is taken
     * 
     * @return table of contents
     */
    public Viewable createTableOfContents(final ViewableContainer from);
}
