package openfasttrack.core.xml;

import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import openfasttrack.core.Location;

public class ContentHandlerAdapter extends DefaultHandler implements ContentHandlerAdapterController
{
    private final static Logger LOG = Logger.getLogger(ContentHandlerAdapter.class.getName());

    private final String fileName;
    private final XMLReader xmlReader;
    private final EventContentHandler delegate;
    private Locator locator;
    private ContentHandler originalContentHandler;

    public ContentHandlerAdapter(final String fileName, final XMLReader xmlReader,
            final EventContentHandler delegate)
    {
        this.fileName = fileName;
        this.xmlReader = xmlReader;
        this.delegate = delegate;
    }

    public void registerListener()
    {
        if (this.originalContentHandler != null)
        {
            throw new IllegalStateException("Already registered as listener");
        }
        this.originalContentHandler = this.xmlReader.getContentHandler();
        this.delegate.init(this);
        this.xmlReader.setContentHandler(this);
    }

    @Override
    public void delegateToSubHandler(final EventContentHandler subDelegate)
    {
        new ContentHandlerAdapter(this.fileName, this.xmlReader, subDelegate).registerListener();
    }

    @Override
    public void setDocumentLocator(final Locator locator)
    {
        this.locator = locator;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName,
            final Attributes attributes) throws SAXException
    {
        LOG.finest(() -> "Start element: uri=" + uri + ", localName=" + localName + ", qName="
                + qName);
        final StartElementEvent event = new StartElementEvent(uri, localName, qName, attributes,
                getCurrentLocation());
        this.delegate.startElement(event);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException
    {
        LOG.finest(
                () -> "End element: uri=" + uri + ", localName=" + localName + ", qName=" + qName);
        final EndElementEvent event = new EndElementEvent(uri, localName, qName,
                getCurrentLocation());
        this.delegate.endElement(event);
    }

    private Location getCurrentLocation()
    {
        return Location.create(this.fileName, this.locator.getLineNumber(),
                this.locator.getColumnNumber());
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
    {
        this.delegate.characters(new String(ch, start, length));
    }

    @Override
    public void parsingFinished()
    {
        if (this.originalContentHandler == null)
        {
            throw new IllegalStateException("No original handler");
        }
        this.xmlReader.setContentHandler(this.originalContentHandler);
    }
}
