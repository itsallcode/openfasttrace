package openfasttrack.importer.specobject;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import openfasttrack.core.SpecificationItemId;
import openfasttrack.core.SpecificationItemId.Builder;
import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.ImporterException;

class SingleSpecobjectImportHelper
{
    private final static Logger LOG = Logger
            .getLogger(SingleSpecobjectImportHelper.class.getName());
    private final XMLEventReader xmlEventReader;
    private final ImportEventListener listener;
    private final Builder idBuilder;

    public SingleSpecobjectImportHelper(final XMLEventReader xmlEventReader,
            final ImportEventListener listener, final String defaultDoctype)
    {
        this.idBuilder = new SpecificationItemId.Builder() //
                .artifactType(defaultDoctype);
        this.xmlEventReader = xmlEventReader;
        this.listener = listener;
    }

    void runImport() throws XMLStreamException
    {
        this.listener.beginSpecificationItem();
        while (this.xmlEventReader.hasNext())
        {
            final XMLEvent currentEvent = this.xmlEventReader.nextEvent();
            switch (currentEvent.getEventType())
            {
            case XMLStreamConstants.START_ELEMENT:
                foundStartElement(currentEvent.asStartElement());
                break;
            case XMLStreamConstants.END_ELEMENT:
                if (currentEvent.asEndElement().getName().getLocalPart().equals("specobject"))
                {
                    final SpecificationItemId id = this.idBuilder.build();
                    LOG.finest(() -> "Specobject element closed: build id " + id);
                    this.listener.setId(id);
                    return;
                }
            default:
                break;
            }
        }
    }

    private void foundStartElement(final StartElement element)
    {
        switch (element.getName().getLocalPart())
        {
        case "id":
            final String id = readCharacterData(element);
            LOG.finest(() -> "Found spec object id " + id);
            this.idBuilder.name(id);
            break;
        case "version":
            final int version = readIntCharacterData(element);
            LOG.finest(() -> "Found spec object version " + version);
            this.idBuilder.revision(version);
            break;
        case "description":
            this.listener.appendDescription(readCharacterData(element));
            break;
        case "rationale":
            this.listener.appendRationale(readCharacterData(element));
            break;
        case "comment":
            this.listener.appendComment(readCharacterData(element));
            break;
        case "needscoverage":
            readNeedsCoverage(element);
            break;
        case "providescoverage":
            readProvidesCoverage(element);
            break;
        case "dependencies":
            readDependencies(element);
            break;

        default:
            LOG.warning(() -> "Found unknown start element " + element.getName());
            break;
        }
    }

    private void readDependencies(final StartElement element)
    {
        readElementUntilEnd(element, (childElement) -> {
            if (childElement.getName().getLocalPart().equals("dependson"))
            {
                final String idString = readCharacterData(childElement);
                LOG.finest(() -> "Found depends on id '" + idString + "'");
                this.listener.addDependsOnId(SpecificationItemId.parseId(idString));
            }
        });
    }

    private void readProvidesCoverage(final StartElement element)
    {
        readElementUntilEnd(element, (childElement) -> {
            if (childElement.getName().getLocalPart().equals("provcov"))
            {
                readProvCov(childElement);
            }
        });
    }

    private void readProvCov(final StartElement element)
    {
        final Builder providesCoverageId = new Builder();
        readElementUntilEnd(element, (childElement) -> {
            final String elementName = childElement.getName().getLocalPart();
            if (elementName.equals("linksto"))
            {
                providesCoverageId.name(readCharacterData(childElement));
            }
            if (elementName.equals("dstversion"))
            {
                providesCoverageId.revision(readIntCharacterData(childElement));
            }
        });

        final SpecificationItemId id = providesCoverageId.build();
        LOG.finest(() -> "Found provides coverage '" + id + "'");
        this.listener.addCoveredId(id);
    }

    private void readNeedsCoverage(final StartElement element)
    {
        readElementUntilEnd(element, (childElement) -> {
            if (childElement.getName().getLocalPart().equals("needsobj"))
            {
                final String artifactType = readCharacterData(childElement);
                LOG.finest(() -> "Found needs artifact type '" + artifactType + "'");
                this.listener.addNeededArtifactType(artifactType);
            }
        });
    }

    private void readElementUntilEnd(final StartElement element,
            final Consumer<StartElement> consumer)
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

    private int readIntCharacterData(final StartElement element)
    {
        return Integer.parseInt(readCharacterData(element));
    }

    private String readCharacterData(final StartElement element)
    {
        final XMLEvent characterEvent = peekNextEvent();
        if (characterEvent == null || !characterEvent.isCharacters())
        {
            throw new ImporterException("No character data found for element " + element);
        }
        final String data = characterEvent.asCharacters().getData().trim();
        LOG.finest(() -> "Found char data for element '" + element.getName() + "': '" + data + "'");
        return data;
    }

    private XMLEvent readNextEvent()
    {
        try
        {
            return this.xmlEventReader.nextEvent();
        } catch (final XMLStreamException e)
        {
            throw new ImporterException("Exception reading next event", e);
        }
    }

    private XMLEvent peekNextEvent()
    {
        try
        {
            return this.xmlEventReader.peek();
        } catch (final XMLStreamException e)
        {
            throw new ImporterException("Exception when peeking next event", e);
        }
    }
}
