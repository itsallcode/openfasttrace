package org.itsallcode.openfasttrace.importer.xmlparser;

import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX content handler that forwards events from an {@link XMLReader} to an
 * {@link EventContentHandler}.
 */
class ContentHandlerAdapter extends DefaultHandler implements ContentHandlerAdapterController {
    private static final Logger LOG = Logger.getLogger(ContentHandlerAdapter.class.getName());

    private final String filePath;
    private final XMLReader xmlReader;
    private final EventContentHandler delegate;
    private Locator locator;

    /**
     * Create a new instance.
     * 
     * @param filePath
     *                  the path of the parsed file.
     * @param xmlReader
     *                  the {@link XMLReader}.
     * @param delegate
     *                  the content handler to which the parsing events should be
     *                  forwared.
     */
    ContentHandlerAdapter(final String filePath, final XMLReader xmlReader,
            final EventContentHandler delegate) {
        this.filePath = filePath;
        this.xmlReader = xmlReader;
        this.delegate = delegate;
    }

    /**
     * Initialize the delegate and register XML content handler.
     */
    void registerListener() {
        verifyNoContentHandlerRegistered();
        this.delegate.init(this);
        this.xmlReader.setContentHandler(this);
    }

    private void verifyNoContentHandlerRegistered() {
        final ContentHandler originalContentHandler = this.xmlReader.getContentHandler();
        if (originalContentHandler != null) {
            throw new IllegalStateException("An XML content handler is already registered: " + originalContentHandler);
        }
    }

    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName,
            final Attributes attributes) throws SAXException {
        LOG.finest(() -> "Start element: uri=" + uri + ", localName=" + localName + ", qName="
                + qName);
        final StartElementEvent event = StartElementEvent.create(uri, localName, qName, attributes,
                getCurrentLocation());
        this.delegate.startElement(event);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException {
        LOG.finest(
                () -> "End element: uri=" + uri + ", localName=" + localName + ", qName=" + qName);
        final EndElementEvent event = EndElementEvent.create(uri, localName, qName,
                getCurrentLocation());
        this.delegate.endElement(event);
    }

    private Location getCurrentLocation() {
        return org.itsallcode.openfasttrace.api.core.Location.create(this.filePath, this.locator.getLineNumber(),
                this.locator.getColumnNumber());
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.delegate.characters(new String(ch, start, length));
    }

    @Override
    public void parsingFinished() {
        this.xmlReader.setContentHandler(null);
    }
}
