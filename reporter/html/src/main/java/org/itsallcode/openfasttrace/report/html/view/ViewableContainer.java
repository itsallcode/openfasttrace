package org.itsallcode.openfasttrace.report.html.view;

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

import java.util.List;

/**
 * A container for viewable elements
 */
public interface ViewableContainer extends Viewable
{
    /**
     * Get the ID of this view element
     * 
     * @return unique ID through which the element can be referenced
     */
    public String getId();

    /**
     * Get the title of this view element
     * 
     * @return title of the view element
     */
    public String getTitle();

    /**
     * Check if the view element can be referenced
     * 
     * @return <code>true</code> if the view element can be referenced
     */
    public boolean isReferenceable();

    /**
     * Add a viewable element
     * 
     * @param child
     *            contained viewable element
     */
    public void add(Viewable child);

    /**
     * Get the list of children of the viewable container
     * 
     * @return list of children
     */
    public List<Viewable> getChildren();
}