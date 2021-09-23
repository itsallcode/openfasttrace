package org.itsallcode.openfasttrace.importer.specobject.xml.tree;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

/**
 * This interface allows {@link TreeContentHandler}s to control the parsing
 * process, e.g. by registering a delegate or stop parsing.
 */
public interface TreeParsingController
{
    /**
     * Set a new handler delegate.
     * 
     * @param newDelegate
     *            the new delegate.
     */
    void setDelegate(TreeContentHandler newDelegate);

    /**
     * @return the currently parsed element node.
     */
    TreeElement getCurrentElement();

    /**
     * Tell the controller to stop parsing, e.g. in case of a parsing error.
     */
    void stopParsing();
}
