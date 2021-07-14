package org.itsallcode.openfasttrace.exporter.common;

/*-
 * #%L
 * OpenFastTrace Specobject Exporter
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

class DelegatingXMLStreamWriter implements XMLStreamWriter
{
    private final XMLStreamWriter writer;

    public DelegatingXMLStreamWriter(XMLStreamWriter writer)
    {
        this.writer = writer;
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException
    {
        writer.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException
    {
        writer.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException
    {
        writer.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException
    {
        writer.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException
    {
        writer.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException
    {
        writer.writeEmptyElement(localName);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        writer.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        writer.writeEndDocument();
    }

    @Override
    public void close() throws XMLStreamException
    {
        writer.close();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        writer.flush();
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException
    {
        writer.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException
    {
        writer.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException
    {
        writer.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException
    {
        writer.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException
    {
        writer.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException
    {
        writer.writeComment(data);
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException
    {
        writer.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException
    {
        writer.writeCData(data);
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException
    {
        writer.writeDTD(dtd);
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException
    {
        writer.writeEntityRef(name);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        writer.writeStartDocument();
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException
    {
        writer.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException
    {
        writer.writeStartDocument(encoding, version);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException
    {
        writer.writeCharacters(text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException
    {
        writer.writeCharacters(text, start, len);
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException
    {
        return writer.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException
    {
        writer.setPrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException
    {
        writer.setDefaultNamespace(uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException
    {
        writer.setNamespaceContext(context);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return writer.getNamespaceContext();
    }

    @Override
    public Object getProperty(String name)
    {
        return writer.getProperty(name);
    }
}
