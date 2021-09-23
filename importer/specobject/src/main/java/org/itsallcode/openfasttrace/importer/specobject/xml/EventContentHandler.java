package org.itsallcode.openfasttrace.importer.specobject.xml;

import org.itsallcode.openfasttrace.importer.specobject.xml.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.specobject.xml.event.StartElementEvent;

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
 * An event handler for XML parsing events.
 */
public interface EventContentHandler
{
    /**
     * Called before parsing begins.
     * 
     * @param contentHandlerAdapter
     *            the controller.
     */
    void init(ContentHandlerAdapterController contentHandlerAdapter);

    /**
     * Called when an XML element starts.
     * 
     * @param event
     *            the start event.
     */
    void startElement(StartElementEvent event);

    /**
     * Called when an XML element ends.
     * 
     * @param event
     *            the end event.
     */
    void endElement(EndElementEvent event);

    /**
     * Called when character data content is found.
     * 
     * @param characters
     *            the character data.
     */
    void characters(String characters);
}
