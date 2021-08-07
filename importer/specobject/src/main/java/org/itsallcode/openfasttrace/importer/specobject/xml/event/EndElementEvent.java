package org.itsallcode.openfasttrace.importer.specobject.xml.event;

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

import javax.xml.namespace.QName;

import org.itsallcode.openfasttrace.api.core.Location;

/**
 * A model for a SAX end element event.
 * 
 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
 */
public class EndElementEvent
{
    private final QName qName;
    private final Location location;

    private EndElementEvent(final QName qName, final Location location)
    {
        this.location = location;
        this.qName = qName;
    }

    /**
     * Creates a new end element event.
     * 
     * @param uri
     *            the namespace URI of the element name.
     * @param localName
     *            the local element name.
     * @param qName
     *            the qname of the element.
     * @param location
     *            the location in the document.
     * @return a new end element event.
     */
    public static EndElementEvent create(final String uri, final String localName,
            final String qName, final Location location)
    {
        final QName qualifiedName = QNameFactory.create(uri, localName, qName);
        return new EndElementEvent(qualifiedName, location);
    }

    /**
     * @return the {@link QName} of the element.
     */
    public QName getName()
    {
        return this.qName;
    }

    /**
     * @return the {@link Location} of the element.
     */
    public Location getLocation()
    {
        return this.location;
    }

    @Override
    public String toString()
    {
        return "EndElementEvent [qName=" + this.qName + ", location=" + this.location + "]";
    }
}
