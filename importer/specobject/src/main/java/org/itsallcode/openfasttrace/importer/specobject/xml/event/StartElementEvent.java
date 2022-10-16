package org.itsallcode.openfasttrace.importer.specobject.xml.event;

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
     * Get the {@link QName} of the element.
     * 
     * @return the {@link QName} of the element.
     */
    public QName getName()
    {
        return this.qName;
    }

    /**
     * Get the {@link Location} of the element.
     * 
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
