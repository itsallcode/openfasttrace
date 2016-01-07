package openfasttrack.importer.specobject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import openfasttrack.core.SpecificationItem;

class ImportHelper
{
    private final static Logger LOG = Logger.getLogger(ImportHelper.class.getName());
    private final XMLEventReader xmlEventReader;
    private final List<SpecificationItem> items;

    private XMLEvent currentEvent;

    public ImportHelper(final XMLEventReader xmlEventReader)
    {
        this.xmlEventReader = xmlEventReader;
        this.items = new ArrayList<>();
    }

    void runImport() throws XMLStreamException
    {
        while (this.xmlEventReader.hasNext())
        {
            this.currentEvent = this.xmlEventReader.nextEvent();
            LOG.finest(() -> "Event: " + this.currentEvent);
        }
    }

    public List<SpecificationItem> getItems()
    {
        return this.items;
    }
}
