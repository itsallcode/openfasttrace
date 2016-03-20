package openfasttrack.exporter.specobject;

import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.Exporter;
import openfasttrack.exporter.ExporterException;

public class SpecobjectExporter implements Exporter
{
    private final XMLStreamWriter xmlWriter;
    private final Collection<LinkedSpecificationItem> items;

    public SpecobjectExporter(final Collection<LinkedSpecificationItem> items,
            final XMLStreamWriter xmlWriter)
    {
        this.items = items;
        this.xmlWriter = xmlWriter;
    }

    @Override
    public void runExport()
    {
        try
        {
            writeOutput();
        }
        catch (final XMLStreamException e)
        {
            throw new ExporterException("Error exporting to specobject format", e);
        }
    }

    private void writeOutput() throws XMLStreamException
    {
        this.xmlWriter.writeStartDocument("UTF-8", "1.0");
        this.xmlWriter.writeStartElement("specobjects");
        this.xmlWriter.writeAttribute("doctype", "doctype");
        this.xmlWriter.writeEndElement();
        this.xmlWriter.writeEndDocument();
    }
}
