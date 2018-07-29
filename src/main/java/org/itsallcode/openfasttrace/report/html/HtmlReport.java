package org.itsallcode.openfasttrace.report.html;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.ReportVerbosity;
import org.itsallcode.openfasttrace.report.Reportable;
import org.itsallcode.openfasttrace.report.view.ViewFactory;
import org.itsallcode.openfasttrace.report.view.Viewable;
import org.itsallcode.openfasttrace.report.view.ViewableContainer;
import org.itsallcode.openfasttrace.report.view.html.HtmlViewFactory;

public class HtmlReport implements Reportable
{
    private final Trace trace;
    private static final String REPORT_CSS_FILE = "/css/report.css";

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
    public void renderToStreamWithVerbosityLevel(final OutputStream outputStream,
            final ReportVerbosity verbosity)
    {
        final ViewFactory factory = HtmlViewFactory.create(outputStream, getCssUrl());
        final ViewableContainer view = factory.createView("", "Specification items by title");
        final List<LinkedSpecificationItem> items = this.trace.getItems();
        items.sort(Comparator.comparing(LinkedSpecificationItem::getTitleWithFallback));
        String initial = "\0";
        ViewableContainer section = factory.createSection(initial, initial);
        for (final LinkedSpecificationItem item : items)
        {
            final String currentInitial = getInitial(item);
            if (!initial.equals(currentInitial))
            {
                initial = currentInitial;
                section = factory.createSection(initial, initial);
                view.add(section);
            }
            final Viewable itemView = factory.createSpecificationItem(item);
            section.add(itemView);
        }
        view.render();
    }

    protected String getInitial(final LinkedSpecificationItem item)
    {
        return item.getTitleWithFallback().substring(0, 1).toUpperCase();
    }
}