package org.itsallcode.openfasttrace.core.xml.event;

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

public class Attribute
{
    private final String qName;
    private final String value;

    private Attribute(final String qName, final String value)
    {
        this.qName = qName;
        this.value = value;
    }

    public String getQname()
    {
        return this.qName;
    }

    public String getValue()
    {
        return this.value;
    }

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
