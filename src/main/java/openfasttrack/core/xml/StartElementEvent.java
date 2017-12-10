package openfasttrack.core.xml;

import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.Attributes;

import openfasttrack.core.Location;

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

    public static StartElementEvent create(final String uri, final String localName,
            final String qName, final Attributes attributes, final Location location)
    {
        final Map<String, Attribute> attributeMap = Attribute.buildMap(attributes);
        final QName qualifiedName = new QName(uri, localName, "");
        return new StartElementEvent(qualifiedName, attributeMap, location);
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
        return this.attributeMap.get(name);
    }

    @Override
    public String toString()
    {
        return "StartElementEvent [qName=" + this.qName + ", attributeMap=" + this.attributeMap
                + ", location=" + this.location + "]";
    }
}
