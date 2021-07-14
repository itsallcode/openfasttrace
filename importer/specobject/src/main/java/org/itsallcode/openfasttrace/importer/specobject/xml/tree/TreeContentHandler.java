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
 * A callback interface for handling XML parsing events.
 */
public interface TreeContentHandler
{
    /**
     * Called before the parsing is started.
     * 
     * @param parsingController
     *            the controller.
     */
    void init(TreeParsingController parsingController);

    /**
     * Called when a new XML element starts.
     * 
     * @param treeElement
     *            the starting element.
     */
    void startElement(TreeElement treeElement);

    /**
     * Called when a XML element is closed.
     * 
     * @param closedElement
     *            the closed element.
     */
    void endElement(TreeElement closedElement);

}
