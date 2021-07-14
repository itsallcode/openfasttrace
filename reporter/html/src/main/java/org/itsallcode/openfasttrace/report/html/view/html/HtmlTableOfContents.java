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

import org.itsallcode.openfasttrace.report.html.view.Viewable;
import org.itsallcode.openfasttrace.report.html.view.ViewableContainer;

class HtmlTableOfContents implements Viewable
{

    private final PrintStream stream;
    private final ViewableContainer from;

    HtmlTableOfContents(final PrintStream outputStream, final ViewableContainer from)
    {
        this.stream = outputStream;
        this.from = from;
    }

    @Override
    public void render(final int level)
    {
        boolean first = true;
        for (final Viewable view : this.from.getChildren())
        {
            renderTableOfContentsItem(view, first);
            first = false;
        }
    }

    protected void renderTableOfContentsItem(final Viewable view, final boolean first)
    {
        if (view instanceof ViewableContainer)
        {
            renderContainerItem(view, first);
        }
    }

    protected void renderContainerItem(final Viewable view, final boolean first)
    {
        final ViewableContainer container = (ViewableContainer) view;
        if (container.isReferenceable())
        {
            renderSeparator(first);
            renderLinkWithText(container);
        }
    }

    protected void renderSeparator(final boolean first)
    {
        this.stream.print(first ? " | " : " &middot; ");
    }

    protected void renderLinkWithText(final ViewableContainer container)
    {
        this.stream.print("<a href=\"#");
        this.stream.print(container.getId());
        this.stream.print("\">");
        this.stream.print(container.getTitle());
        this.stream.print("</a>");
    }
}