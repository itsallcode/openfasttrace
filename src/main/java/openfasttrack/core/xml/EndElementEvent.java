package openfasttrack.core.xml;

import javax.xml.namespace.QName;

import openfasttrack.core.Location;

public class EndElementEvent
{
    private final QName qName;
    private final Location location;

    private EndElementEvent(final QName qName, final Location location)
    {
        this.location = location;
        this.qName = qName;
    }

    public static EndElementEvent create(final String uri, final String localName,
            final String qName, final Location location)
    {
        final QName qualifiedName = new QName(uri, localName, "");
        return new EndElementEvent(qualifiedName, location);
    }

    public QName getName()
    {
        return this.qName;
    }

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
