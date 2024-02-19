package org.itsallcode.openfasttrace.importer.xmlparser.event;

import javax.xml.namespace.QName;

import org.itsallcode.openfasttrace.api.core.Location;

/**
 * A model for a SAX end element event.
 * 
 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
 */
public class EndElementEvent {
    private final QName qName;
    private final Location location;

    private EndElementEvent(final QName qName, final Location location) {
        this.location = location;
        this.qName = qName;
    }

    /**
     * Creates a new end element event.
     * 
     * @param uri
     *                  the namespace URI of the element name.
     * @param localName
     *                  the local element name.
     * @param qName
     *                  the qname of the element.
     * @param location
     *                  the location in the document.
     * @return a new end element event.
     */
    public static EndElementEvent create(final String uri, final String localName,
            final String qName, final Location location) {
        final QName qualifiedName = QNameFactory.create(uri, localName, qName);
        return new EndElementEvent(qualifiedName, location);
    }

    /**
     * Get the {@link QName} of the element.
     * 
     * @return the {@link QName} of the element.
     */
    public QName getName() {
        return this.qName;
    }

    /**
     * Get the {@link Location} of the element.
     * 
     * @return the {@link Location} of the element.
     */
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "EndElementEvent [qName=" + this.qName + ", location=" + this.location + "]";
    }
}
