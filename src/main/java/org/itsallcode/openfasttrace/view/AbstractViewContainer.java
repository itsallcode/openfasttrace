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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

/**
 * Abstract base class for all containers of viewable elements
 */
public abstract class AbstractViewContainer implements ViewableContainer
{
    protected List<Viewable> children;

    /**
     * Create a new instance of type {@link AbstractViewContainer}.
     */
    public AbstractViewContainer()
    {
        this.children = new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.itsallcode.openfasttrace.view.Viewable#render(int)
     */
    @Override
    public void render(final int level)
    {
        renderBeforeChildren(level);
        renderChildren(level);
        renderAfterChildren(level);
    }

    /**
     * Render a the part of the view that comes before the children.
     * 
     * @param level
     *            indentation level
     */
    protected void renderBeforeChildren(final int level)
    {
        // Default implementation left empty on purpose.
    }

    /**
     * Render a the children of this sub(view).
     * 
     * @param level
     *            indentation level
     */
    protected void renderChildren(final int level)
    {
        for (final Viewable child : this.children)
        {
            child.render(level + 1);
        }
    }

    /**
     * Render a the part of the view that comes after the children.
     * 
     * @param level
     *            indentation level
     */
    protected void renderAfterChildren(final int level)
    {
        // Default implementation left empty on purpose.
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.itsallcode.openfasttrace.view.ViewableContainer#add(org.itsallcode.
     * openfasttrace.view.Viewable)
     */
    @Override
    public void add(final Viewable child)
    {
        this.children.add(child);
    }

    @Generated(value = "org.eclipse.Eclipse")
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.children == null) ? 0 : this.children.hashCode());
        return result;
    }

    @Generated(value = "org.eclipse.Eclipse")
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof AbstractViewContainer))
        {
            return false;
        }
        final AbstractViewContainer other = (AbstractViewContainer) obj;
        if (this.children == null)
        {
            if (other.children != null)
            {
                return false;
            }
        }
        else if (!this.children.equals(other.children))
        {
            return false;
        }
        return true;
    }
}
