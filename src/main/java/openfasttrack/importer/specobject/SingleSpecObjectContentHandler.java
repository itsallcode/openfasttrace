package openfasttrack.importer.specobject;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.xml.stream.events.XMLEvent;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import openfasttrack.core.Location;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.SpecificationItemId.Builder;
import openfasttrack.core.xml.EndElementEvent;
import openfasttrack.core.xml.StartElementEvent;
import openfasttrack.importer.ImportEventListener;

public class SingleSpecObjectContentHandler extends DefaultHandler
{
    private final static Logger LOG = Logger
            .getLogger(SingleSpecObjectContentHandler.class.getName());

    private final String fileName;
    private final XMLReader xmlReader;
    private final ImportEventListener listener;
    private final ContentHandler originalContentHandler;
    private final Builder idBuilder;

    private Locator locator;
    private final StringBuilder stringBuilder = new StringBuilder();
    private Builder providesCoverageId;
    private StartElementEvent waitingForEndElement;

    public SingleSpecObjectContentHandler(final String fileName, final XMLReader xmlReader,
            final ImportEventListener listener, final String defaultDoctype)
    {
        this.fileName = fileName;
        this.xmlReader = xmlReader;
        this.listener = listener;
        this.idBuilder = new SpecificationItemId.Builder() //
                .artifactType(defaultDoctype);
        this.originalContentHandler = xmlReader.getContentHandler();
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
        foundStartElement(currentEvent);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
            throws SAXException
    {
        final EndElementEvent currentEvent = new EndElementEvent(uri, localName, qName,
                getCurrentLocation());

        if ("specobject".equals(currentEvent.getName().getLocalPart()))
        {
            processSpecobjectId();
            return;
        }

        switch (localName)
        {
        case "id":
            final String id = readCharacterData(currentEvent);
            LOG.finest(() -> "Found spec object id " + id);
            this.idBuilder.name(id);
            this.listener.setLocation(this.fileName, currentEvent.getLocation().getLine());
            break;
        case "version":
            final int version = readIntCharacterData(currentEvent);
            LOG.finest(() -> "Found spec object version " + version);
            this.idBuilder.revision(version);
            break;
        case "description":
            this.listener.appendDescription(readCharacterData(currentEvent));
            break;
        case "rationale":
            this.listener.appendRationale(readCharacterData(currentEvent));
            break;
        case "comment":
            this.listener.appendComment(readCharacterData(currentEvent));
            break;
        case "needscoverage":
            readNeedsCoverage(currentEvent);
            break;
        case "providescoverage":
            readProvidesCoverage(currentEvent);
            break;
        case "dependencies":
            readDependencies(currentEvent);
            break;

        default:
            LOG.warning(
                    () -> "Found unknown start element '" + currentEvent.getName() + "': ignore.");
            break;
        }
    }

    private Location getCurrentLocation()
    {
        return Location.create(this.fileName, this.locator.getLineNumber(),
                this.locator.getColumnNumber());
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException
    {
        this.stringBuilder.append(new String(ch, start, length));
    }

    private void processSpecobjectId()
    {
        final SpecificationItemId id = this.idBuilder.build();
        LOG.finest(() -> "Specobject element closed: build id " + id);
        this.listener.setId(id);
        this.listener.endSpecificationItem();
    }

    private void foundStartElement(final StartElementEvent element)
    {
        switch (element.getName().getLocalPart())
        {
        case "description":
            readCharacterData(element);
            this.listener.appendDescription(readCharacterData(element));
            break;
        case "rationale":
            readCharacterData(element);
            this.listener.appendRationale(readCharacterData(element));
            break;
        case "comment":
            readCharacterData(element);
            this.listener.appendComment(readCharacterData(element));
            break;
        case "needscoverage":
            readCharacterData(element);
            readNeedsCoverage(element);
            break;
        case "providescoverage":
            readCharacterData(element);
            readProvidesCoverage(element);
            break;
        case "dependencies":
            readCharacterData(element);
            readDependencies(element);
            break;

        default:
            LOG.warning(() -> "Found unknown start element '" + element.getName() + "': ignore.");
            break;
        }
    }

    private void readCharacterDataForElement(final StartElementEvent element)
    {
        if (this.waitingForEndElement != null)
        {
            throw new IllegalStateException("Reading character data for " + element
                    + " but already waiting for end of " + this.waitingForEndElement);
        }
        this.waitingForEndElement = element;
    }

    private void readDependencies(final StartElementEvent element)
    {
        readElementUntilEnd(element, childElement -> {
            if ("dependson".equals(childElement.getName().getLocalPart()))
            {
                final String idString = readCharacterData(childElement);
                LOG.finest(() -> "Found depends on id '" + idString + "'");
                this.listener.addDependsOnId(SpecificationItemId.parseId(idString));
            }
        });
    }

    private void readProvidesCoverage(final E element)
    {
        readElementUntilEnd(element, childElement -> {
            if ("provcov".equals(childElement.getName().getLocalPart()))
            {
                readProvCov(childElement);
            }
        });
    }

    private void readProvCov(final StartElementEvent element)
    {
        final Builder providesCoverageId = new Builder();
        readElementUntilEnd(element, childElement -> {
            final String elementName = childElement.getName().getLocalPart();
            if ("linksto".equals(elementName))
            {
                providesCoverageId.name(readCharacterData(childElement));
            }
            if ("dstversion".equals(elementName))
            {
                providesCoverageId.revision(readIntCharacterData(childElement));
            }
        });

        final SpecificationItemId id = providesCoverageId.build();
        LOG.finest(() -> "Found provides coverage '" + id + "'");
        this.listener.addCoveredId(id);
    }

    private void readNeedsCoverage(final EndElementEvent element)
    {
        final String artifactType = readCharacterData(element);
        LOG.finest(() -> "Found needs artifact type '" + artifactType + "'");
        this.listener.addNeededArtifactType(artifactType);
    }

    private void readElementUntilEnd(final StartElementEvent element,
            final Consumer<StartElementEvent> consumer)
    {
        while (this.xmlEventReader.hasNext())
        {
            final XMLEvent currentEvent = readNextEvent();

            if (currentEvent.isEndElement()
                    && currentEvent.asEndElement().getName().equals(element.getName()))
            {
                LOG.finest(() -> "Found end event of element " + element + ": stop processing");
                return;
            }
            if (currentEvent.isStartElement())
            {
                consumer.accept(currentEvent.asStartElement());
            }
        }
    }

    private int readIntCharacterData(final EndElementEvent element)
    {
        return Integer.parseInt(readCharacterData(element));
    }

    private String readCharacterData(final EndElementEvent element)
    {
        final String data = this.stringBuilder.toString();
        this.stringBuilder = new StringBuilder();

        LOG.finest(() -> "Found char data for element '" + element.getName() + "': '" + data + "'");
        return data;
    }
}
