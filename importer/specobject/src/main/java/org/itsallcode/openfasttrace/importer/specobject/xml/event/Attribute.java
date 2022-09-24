package org.itsallcode.openfasttrace.importer.specobject.xml.event;

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
     * Get the qualified name of this attribute.
     * 
     * @return {@link Attributes#getQName(int)}
     */
    public String getQname()
    {
        return this.qName;
    }

    /**
     * Get the attribute value.
     * 
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
