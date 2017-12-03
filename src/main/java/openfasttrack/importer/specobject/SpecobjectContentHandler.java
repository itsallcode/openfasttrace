package openfasttrack.importer.specobject;

import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import openfasttrack.core.Location;
import openfasttrack.core.xml.Attribute;
import openfasttrack.core.xml.EndElementEvent;
import openfasttrack.core.xml.StartElementEvent;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.ImporterException;

class SpecobjectContentHandler extends DefaultHandler
{
    private final static Logger LOG = Logger.getLogger(SpecobjectContentHandler.class.getName());
    private static final String SPECDOCUMENT_ROOT_ELEMENT_NAME = "specdocument";
    private static final String SPECOBJECTS_ELEMENT_NAME = "specobjects";
    private static final String SPECOBJECT_ELEMENT_NAME = "specobject";
    private static final String DOCTYPE_ATTRIBUTE_NAME = "doctype";

    private final ImportEventListener listener;
    private boolean validRootElementFound;
    private Locator locator;
    private final String fileName;
    private String defaultDoctype;
    private final XMLReader xmlReader;

    public SpecobjectContentHandler(final String fileName, final XMLReader xmlReader,
            final ImportEventListener listener)
    {
        this.fileName = fileName;
        this.xmlReader = xmlReader;
        this.listener = listener;
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

        final StartElementEvent currentEvent = new StartElementEvent(uri, localName, qName,
                attributes, getCurrentLocation());
        if (isXmlRootWhitelisted(currentEvent))
        {
            return;
        }
        foundStartElement(currentEvent);
    }

    private Location getCurrentLocation()
    {
        return Location.create(this.fileName, this.locator.getLineNumber(),
                this.locator.getColumnNumber());
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException
    {
        final EndElementEvent currentEvent = new EndElementEvent(uri, localName, qName,
                getCurrentLocation());
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
    }

    private boolean isXmlRootWhitelisted(final StartElementEvent currentElement)
    {
        if (this.validRootElementFound)
        {
            return false;
        }
        if (isWhitelisted(currentElement))
        {
            this.validRootElementFound = true;
            return false;
        }
        logUnkownElement(currentElement, "skip file");
        return true;
    }

    private boolean isWhitelisted(final StartElementEvent rootElement)
    {
        final String elementName = rootElement.getName().getLocalPart();
        return SPECDOCUMENT_ROOT_ELEMENT_NAME.equals(elementName);
    }

    private void foundStartElement(final StartElementEvent element)
    {
        switch (element.getName().getLocalPart())
        {
        case SPECDOCUMENT_ROOT_ELEMENT_NAME:
            LOG.finest(() -> "Found XML root element '" + SPECDOCUMENT_ROOT_ELEMENT_NAME + "' at "
                    + element.getLocation());
            break;
        case SPECOBJECTS_ELEMENT_NAME:
            processSpecobjectsContainerElement(element);
            break;
        case SPECOBJECT_ELEMENT_NAME:
            processSpecobjectElement(element);
            break;
        default:
            logUnkownElement(element, "skip element");
            break;
        }
    }

    private void processSpecobjectElement(final StartElementEvent element)
    {
        if (this.defaultDoctype == null)
        {
            throw new ImporterException(
                    "No specobject default doctype found in file '" + this.fileName + "'");
        }
        LOG.finest(() -> "Found XML element '" + SPECOBJECT_ELEMENT_NAME
                + "': import using default doctype '" + this.defaultDoctype + "' from "
                + element.getLocation());
        final SingleSpecObjectContentHandler specObjectContentHandler = new SingleSpecObjectContentHandler(
                this.fileName, this.xmlReader, this.listener, this.defaultDoctype);
        this.xmlReader.setContentHandler(specObjectContentHandler);
    }

    private void processSpecobjectsContainerElement(final StartElementEvent element)
    {
        final Attribute doctypeAttribute = element.getAttributeValueByName(DOCTYPE_ATTRIBUTE_NAME);
        if (doctypeAttribute == null)
        {
            throw new ImporterException("Element " + element + " does not have an attribute '"
                    + DOCTYPE_ATTRIBUTE_NAME + "' at " + element.getLocation());
        }
        this.defaultDoctype = doctypeAttribute.getValue();
        LOG.finest(
                () -> "Found XML element '" + SPECOBJECTS_ELEMENT_NAME + "' with default doctype '"
                        + this.defaultDoctype + "' at " + element.getLocation());
    }

    private void logUnkownElement(final StartElementEvent element, final String consequence)
    {
        LOG.warning(() -> "Found unknown XML element '" + element + "': at " + element.getLocation()
                + ": " + consequence);
    }

}
