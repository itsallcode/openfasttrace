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

import java.io.OutputStream;
import java.io.PrintStream;

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.view.AbstractViewFactory;
import org.itsallcode.openfasttrace.view.Viewable;
import org.itsallcode.openfasttrace.view.ViewableContainer;

public class HtmlViewFactory extends AbstractViewFactory
{
    private final PrintStream stream;

    public HtmlViewFactory(final OutputStream outputStream)
    {
        super(outputStream);
        this.stream = new PrintStream(outputStream);
    }

    @Override
    public ViewableContainer createView(final String id, final String title)
    {
        return new HtmlView(this.stream, id, title);
    }

    @Override
    public ViewableContainer createSection(final String id, final String title)
    {
        return new HtmlSection(this.stream, id, title);
    }

    @Override
    public Viewable createSpecificationItem(final LinkedSpecificationItem item)
    {
        return new HtmlSpecificationItem(this.stream, item);
    }
}
