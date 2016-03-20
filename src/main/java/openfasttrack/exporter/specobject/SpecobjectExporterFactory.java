package openfasttrack.exporter.specobject;

import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.Exporter;
import openfasttrack.exporter.ExporterException;
import openfasttrack.exporter.ExporterFactory;

public class SpecobjectExporterFactory extends ExporterFactory
{
    private final XMLOutputFactory xmlOutputFactory;

    protected SpecobjectExporterFactory()
    {
        super("specobject");
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }

    @Override
    protected Exporter createExporter(final Writer writer,
            final List<LinkedSpecificationItem> items)
    {
        final XMLStreamWriter xmlWriter = createXmlWriter(writer);
        return new SpecobjectExporter(items, xmlWriter);
    }

    private XMLStreamWriter createXmlWriter(final Writer writer)
    {
        XMLStreamWriter xmlWriter;
        try
        {
            xmlWriter = this.xmlOutputFactory.createXMLStreamWriter(writer);
        }
        catch (final XMLStreamException e)
        {
            throw new ExporterException("Error creating xml stream writer for writer " + writer, e);
        }
        return xmlWriter;
    }
}
