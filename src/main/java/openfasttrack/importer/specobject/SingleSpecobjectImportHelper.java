package openfasttrack.importer.specobject;

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
        this.listener.startSpecificationItem();
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
                    LOG.fine(() -> "Specobject element closed: build id " + id);
                    this.listener.setId(id);
                    return;
                }
            default:
                LOG.warning(() -> "Ignore event " + currentEvent);
                break;
            }
        }
    }

    private void foundStartElement(final StartElement element) throws XMLStreamException
    {
        switch (element.getName().getLocalPart())
        {
        case "id":
            final String id = readCharacterData(element);
            LOG.fine(() -> "Found spec object id " + id);
            this.idBuilder.name(id);
            break;
        case "version":
            final int version = readIntCharacterData(element);
            LOG.fine(() -> "Found spec object version " + version);
            this.idBuilder.revision(version);
            break;

        default:
            LOG.warning(() -> "Found unknown start element " + element.getName());
            break;
        }
    }

    private int readIntCharacterData(final StartElement element)
            throws NumberFormatException, XMLStreamException
    {
        return Integer.parseInt(readCharacterData(element));
    }

    private String readCharacterData(final StartElement element) throws XMLStreamException
    {
        final XMLEvent characterEvent = this.xmlEventReader.peek();
        if (characterEvent == null || !characterEvent.isCharacters())
        {
            throw new ImporterException("No character data found for element " + element);
        }
        return characterEvent.asCharacters().getData();
    }
}
