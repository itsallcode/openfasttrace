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

import java.util.Map;

import javax.xml.namespace.QName;

import org.itsallcode.openfasttrace.api.core.Location;
import org.xml.sax.Attributes;

/**
 * A model for a SAX start element event.
 * 
 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
 */
public class StartElementEvent
{
    private final QName qName;
    private final Location location;
    private final Map<String, Attribute> attributeMap;

    private StartElementEvent(final QName qName, final Map<String, Attribute> attributeMap,
            final Location location)
    {
        this.attributeMap = attributeMap;
        this.location = location;
        this.qName = qName;
    }

    /**
     * Creates a new start element event.
     * 
     * @param uri
     *            the namespace URI of the element name.
     * @param localName
     *            the local element name.
     * @param qName
     *            the qname of the element.
     * @param attributes
     *            the attributes of the element.
     * @param location
     *            the location in the document.
     * @return a new start element event.
     */
    public static StartElementEvent create(final String uri, final String localName,
            final String qName, final Attributes attributes, final Location location)
    {
        final Map<String, Attribute> attributeMap = Attribute.buildMap(attributes);
        final QName qualifiedName = QNameFactory.create(uri, localName, qName);
        return new StartElementEvent(qualifiedName, attributeMap, location);
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

    /**
     * The value of the attribute with the given name.
     * 
     * @param name
     *            the attribute's name.
     * @return the attribute value or {@code null} if no attribute exists.
     */
    public Attribute getAttributeValueByName(final String name)
    {
        return this.attributeMap.get(name);
    }

    @Override
    public String toString()
    {
        return "StartElementEvent [qName=" + this.qName + ", attributeMap=" + this.attributeMap
                + ", location=" + this.location + "]";
    }
}
