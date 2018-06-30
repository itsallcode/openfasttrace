package org.itsallcode.openfasttrace.report.view.html;

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

import java.io.PrintStream;

import org.itsallcode.openfasttrace.report.view.AbstractViewContainer;
import org.itsallcode.openfasttrace.report.view.Viewable;

/**
 * Single HTML page view
 */
public class HtmlView extends AbstractViewContainer implements Viewable
{
    private final String title;
    private final PrintStream stream;

    /**
     * Create a new instance of type {@link HtmlView}.
     * 
     * @param id
     *            the view ID
     * @param title
     *            the view title
     */
    public HtmlView(final PrintStream stream, final String id, final String title)
    {
        this.stream = stream;
        this.title = title;
    }

    @Override
    public void renderBeforeChildren(final int level)
    {
        this.stream.println("<!DOCTYPE html>");
        this.stream.println("<html>");
        this.stream.println("  <head>");
        this.stream.println(
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"oft_tracing_report.css\">");
        this.stream.println("    <meta charset=\"UTF-8\">");
        this.stream.print("    <title>");
        this.stream.print(this.title);
        this.stream.println("</title>");
        this.stream.println("  </head>");
        this.stream.println("  <body>");
    }

    @Override
    public void renderAfterChildren(final int level)
    {
        this.stream.println("  </body>");
        this.stream.print("</html>");
    }
}
