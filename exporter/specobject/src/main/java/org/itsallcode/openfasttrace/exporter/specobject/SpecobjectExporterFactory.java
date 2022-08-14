package org.itsallcode.openfasttrace.exporter.specobject;

import java.io.Writer;
import java.util.stream.Stream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.exporter.Exporter;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
import org.itsallcode.openfasttrace.exporter.common.IndentingXMLStreamWriter;

/**
 * {@link ExporterFactory} for creating {@link Exporter}s that support writing
 * specobject output files.
 */
public class SpecobjectExporterFactory extends ExporterFactory
{
    private static final String SUPPORTED_FORMAT = "specobject";
    private final XMLOutputFactory xmlOutputFactory;

    /** Creates a new instance. */
    public SpecobjectExporterFactory()
    {
        super(SUPPORTED_FORMAT);
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }

    @Override
    protected Exporter createExporter(final Writer writer,
            final Stream<SpecificationItem> itemStream, final Newline newline)
    {
        final XMLStreamWriter xmlWriter = createXmlWriter(writer);
        final IndentingXMLStreamWriter indentingXmlWriter = new IndentingXMLStreamWriter(xmlWriter);
        return new SpecobjectExporter(itemStream, indentingXmlWriter, writer, newline);
    }

    private XMLStreamWriter createXmlWriter(final Writer writer)
    {
        try
        {
            return this.xmlOutputFactory.createXMLStreamWriter(writer);
        }
        catch (final XMLStreamException exception)
        {
            throw new ExporterException("Error creating XML stream writer for writer " + writer, exception);
        }
    }
}
