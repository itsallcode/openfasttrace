package org.itsallcode.openfasttrace.view.html;

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

import static org.itsallcode.openfasttrace.view.html.CharacterConstants.CHECKMARK;

import java.io.PrintStream;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.view.Viewable;

public class HtmlSpecificationItem implements Viewable
{
    private final LinkedSpecificationItem item;
    private final PrintStream stream;

    public HtmlSpecificationItem(final PrintStream stream, final LinkedSpecificationItem item)
    {
        this.stream = stream;
        this.item = item;
    }

    @Override
    public void render(final int level)
    {
        final String indentation = IndentationHelper.createIndentationPrefix(level);
        final SpecificationItemId id = this.item.getId();
        this.stream.print(indentation);
        this.stream.print("<section class=\"sitem\" id=\"");
        this.stream.print(id);
        this.stream.println("\">");
        this.stream.print(indentation);
        this.stream.println("  <details>");
        this.stream.print(indentation);
        this.stream.print("    <summary title=\"");
        this.stream.print(id);
        this.stream.print("\">");
        this.stream.print(CHECKMARK);
        this.stream.print("<b>");
        this.stream.print(this.item.getTitleWithFallback());
        this.stream.print("</b><small>, rev. ");
        this.stream.print(id.getRevision());
        this.stream.print(", ");
        this.stream.print(id.getArtifactType());
        this.stream.println("</small></summary>");
        this.stream.print(indentation);
        this.stream.print("    <p class=\"desc\">");
        this.stream.print(this.item.getDescription());
        this.stream.println("</p>");
        this.stream.print(indentation);
        this.stream.println("  </details>");
        this.stream.print(indentation);
        this.stream.println("</section>");
    }
}