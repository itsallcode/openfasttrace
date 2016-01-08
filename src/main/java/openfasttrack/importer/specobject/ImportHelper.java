package openfasttrack.importer.specobject;

import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import openfasttrack.importer.ImportEventListener;

class ImportHelper
{
    private final static Logger LOG = Logger.getLogger(ImportHelper.class.getName());
    private final XMLEventReader xmlEventReader;
    private final ImportEventListener listener;
    private String defaultDoctype;

    public ImportHelper(final XMLEventReader xmlEventReader, final ImportEventListener listener)
    {
        this.xmlEventReader = xmlEventReader;
        this.listener = listener;
    }

    void runImport() throws XMLStreamException
    {
        while (this.xmlEventReader.hasNext())
        {
            final XMLEvent currentEvent = this.xmlEventReader.nextEvent();

            if (currentEvent.isStartElement())
            {
                foundStartElement(currentEvent.asStartElement());
            }
        }
    }

    private void foundStartElement(final StartElement element) throws XMLStreamException
    {
        switch (element.getName().getLocalPart())
        {
        case "specobjects":
            final Attribute doctypeAttribute = element.getAttributeByName(new QName("doctype"));
            if (doctypeAttribute != null)
            {
                this.defaultDoctype = doctypeAttribute.getValue();
                LOG.finest(() -> "Found specobjects with default doctype '" + this.defaultDoctype
                        + "'");
            }
            break;

        case "specobject":
            new SingleSpecobjectImportHelper(this.xmlEventReader, this.listener,
                    this.defaultDoctype).runImport();
            break;

        default:
            LOG.warning(() -> "Found unknown start element " + element.getName());
            break;
        }
    }
}
