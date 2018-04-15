package org.itsallcode.openfasttrace.view;

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

import org.itsallcode.openfasttrace.core.LinkedSpecificationItem;

public abstract class AbstractViewFactory
{
    protected final OutputStream outputStream;

    protected AbstractViewFactory(final OutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    /**
     * Create a view.
     * 
     * @param id
     *            view ID
     * @param title
     *            view title
     * @return view
     */
    public abstract Viewable createView(final String id, final String title);

    /**
     * Create a section.
     * 
     * @param id
     *            section ID
     * @param title
     *            section title
     * @return section
     */
    public abstract Viewable createSection(final String id, final String title);

    /**
     * Create a view element that represents a {@link LinkedSpecificationItem}.
     * 
     * @param item
     *            represented {@link LinkedSpecificationItem}
     * @return view element
     */
    public abstract Viewable createSpecificationItem(final LinkedSpecificationItem item);
}
