package org.itsallcode.openfasttrace.core.xml;

import org.itsallcode.openfasttrace.core.xml.event.EndElementEvent;
import org.itsallcode.openfasttrace.core.xml.event.StartElementEvent;

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

public interface EventContentHandler
{
    void startElement(StartElementEvent event);

    void endElement(EndElementEvent event);

    void characters(String characters);

    void init(ContentHandlerAdapterController contentHandlerAdapter);
}
