package org.itsallcode.openfasttrace.report.html.view.html;

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

import org.itsallcode.openfasttrace.report.html.view.AbstractStreamableViewContainer;
import org.itsallcode.openfasttrace.report.html.view.IndentationHelper;

/**
 * HTML variant of a report section
 */
class HtmlSection extends AbstractStreamableViewContainer
{
    /**
     * Create a new instance of a {@link HtmlSection}
     * 
     * @param stream
     *            stream the section is rendered to
     * @param id
     *            section ID
     * @param title
     *            section title
     */
    HtmlSection(final PrintStream stream, final String id, final String title)
    {
        super(stream, id, title);
    }

    @Override
    protected void renderBeforeChildren(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        final String header = "h" + (level + 1);
        this.stream.print(indentation);
        this.stream.print("<section id=\"");
        this.stream.print(this.getId());
        this.stream.println("\">");
        this.stream.print(indentation);
        this.stream.print("  <");
        this.stream.print(header);
        this.stream.print(">");
        this.stream.print(this.getTitle());
        this.stream.print("</");
        this.stream.print(header);
        this.stream.println(">");
    }

    @Override
    protected void renderAfterChildren(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        this.stream.print(indentation);
        this.stream.println("</section>");
    }
}
