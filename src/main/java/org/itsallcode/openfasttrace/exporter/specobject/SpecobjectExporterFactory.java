package org.itsallcode.openfasttrace.exporter.specobject;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.Writer;
import java.util.stream.Stream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.exporter.Exporter;
import org.itsallcode.openfasttrace.exporter.ExporterException;
import org.itsallcode.openfasttrace.exporter.ExporterFactory;

public class SpecobjectExporterFactory extends ExporterFactory
{
    public static final String SUPPORTED_FORMAT = "specobject";
    private final XMLOutputFactory xmlOutputFactory;

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
        return new SpecobjectExporter(itemStream, xmlWriter, newline);
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
