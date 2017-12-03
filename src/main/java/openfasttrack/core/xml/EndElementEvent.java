package openfasttrack.core.xml;

import javax.xml.namespace.QName;

import openfasttrack.core.Location;

public class EndElementEvent
{
    private final QName qName;
    private final Location location;

    public EndElementEvent(final String uri, final String localName, final String qName,
            final Location location)
    {
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
}
