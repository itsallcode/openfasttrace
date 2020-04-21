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

import java.io.PrintStream;

/**
 * Abstract base class for View Containers that can be rendered to an output
 * stream.
 */
public abstract class AbstractStreamableViewContainer extends AbstractViewContainer
{
    protected final PrintStream stream;

    public AbstractStreamableViewContainer(final PrintStream stream)
    {
        super();
        this.stream = stream;
    }

    public AbstractStreamableViewContainer(final PrintStream stream, final String id,
            final String title)
    {
        super(id, title);
        this.stream = stream;
    }

    public void renderIndentation(final int level)
    {
        this.stream.print(IndentationHelper.createIndentationPrefix(level));
    }
}
