package openfasttrack.exporter.specobject;

import java.util.Collection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.exporter.Exporter;
import openfasttrack.exporter.ExporterException;

public class SpecobjectExporter implements Exporter
{
    private final XMLStreamWriter writer;
    private final Collection<LinkedSpecificationItem> items;

    public SpecobjectExporter(final Collection<LinkedSpecificationItem> items,
            final XMLStreamWriter xmlWriter)
    {
        this.items = items;
        this.writer = xmlWriter;
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
        this.writer.writeStartDocument("UTF-8", "1.0");
        this.writer.writeStartElement("specdocument");
        this.writer.writeStartElement("specobjects");
        this.writer.writeAttribute("doctype", "doctype");
        for (final LinkedSpecificationItem item : this.items)
        {
            writeItem(item.getItem());
        }
        this.writer.writeEndElement();
        this.writer.writeEndElement();
        this.writer.writeEndDocument();
    }

    private void writeItem(final SpecificationItem item) throws XMLStreamException
    {
        this.writer.writeStartElement("specobject");
        writeElement("id", item.getId().getName());
        writeElement("version", item.getId().getRevision());
        writeElement("description", item.getDescription());
        writeElement("rationale", item.getRationale());
        writeElement("comment", item.getComment());

        if (!item.getNeedsArtifactTypes().isEmpty())
        {

            this.writer.writeStartElement("needscoverage");
            for (final String neededArtifactType : item.getNeedsArtifactTypes())
            {
                writeElement("needsobj", neededArtifactType);
            }
            this.writer.writeEndElement();
        }

        if (!item.getCoveredIds().isEmpty())
        {

            this.writer.writeStartElement("providescoverage");
            for (final SpecificationItemId coveredId : item.getCoveredIds())
            {
                this.writer.writeStartElement("provcov");
                writeElement("linksto", coveredId.getName());
                writeElement("dstversion", coveredId.getRevision());
                this.writer.writeEndElement();
            }
            this.writer.writeEndElement();
        }

        if (!item.getDependOnIds().isEmpty())
        {

            this.writer.writeStartElement("dependencies");
            for (final SpecificationItemId dependsOnId : item.getDependOnIds())
            {
                writeElement("dependson", dependsOnId.toString());
            }
            this.writer.writeEndElement();
        }

        this.writer.writeEndElement();
    }

    private void writeElement(final String elementName, final int content) throws XMLStreamException
    {
        writeElement(elementName, String.valueOf(content));
    }

    private void writeElement(final String elementName, final String content)
            throws XMLStreamException
    {
        this.writer.writeStartElement(elementName);
        this.writer.writeCharacters(content);
        this.writer.writeEndElement();
    }
}
