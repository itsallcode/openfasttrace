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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

/**
 * A simplified wrapper for SAX {@link Attributes}.
 */
public class Attribute
{
    private final String qName;
    private final String value;

    private Attribute(final String qName, final String value)
    {
        this.qName = qName;
        this.value = value;
    }

    /**
     * @return {@link Attributes#getQName(int)}
     */
    public String getQname()
    {
        return this.qName;
    }

    /**
     * @return {@link Attributes#getValue(int)}
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * Converts the given {@link Attributes} to a {@link Map}.
     * 
     * @param attr
     *            the attributes to wrap.
     * @return the wrapped attributes.
     */
    public static Map<String, Attribute> buildMap(final Attributes attr)
    {
        final Map<String, Attribute> attributes = new HashMap<>();
        for (int i = 0; i < attr.getLength(); i++)
        {
            final Attribute attribute = new Attribute(attr.getQName(i), attr.getValue(i));
            attributes.put(attribute.getQname(), attribute);
        }
        return attributes;
    }

    @Override
    public String toString()
    {
        return "Attribute [qName=" + this.qName + ", value=" + this.value + "]";
    }
}
