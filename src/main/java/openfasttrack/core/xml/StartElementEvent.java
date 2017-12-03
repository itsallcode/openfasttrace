package openfasttrack.core.xml;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import openfasttrack.core.Location;

public class StartElementEvent
{
    private final QName qName;
    private final Location location;
    private final Attributes attributes;

    public StartElementEvent(final String uri, final String localName, final String qName,
            final Attributes attributes, final Location location)
    {
        this.attributes = attributes;
        this.location = location;
        this.qName = new QName(uri, localName, null);
    }

    public QName getName()
    {
        return this.qName;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public Attribute getAttributeValueByName(final String name)
    {
        return new Attribute(name, this.attributes.getValue(name));
    }
}
