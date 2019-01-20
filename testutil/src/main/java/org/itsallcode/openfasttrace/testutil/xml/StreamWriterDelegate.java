package org.itsallcode.openfasttrace.testutil.xml;

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


import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class StreamWriterDelegate implements XMLStreamWriter
{

    protected StreamWriterDelegate(final XMLStreamWriter out)
    {
        this.out = out;
    }

    protected XMLStreamWriter out;

    @Override
    public Object getProperty(final String name) throws IllegalArgumentException
    {
        return this.out.getProperty(name);
    }

    @Override
    public NamespaceContext getNamespaceContext()
    {
        return this.out.getNamespaceContext();
    }

    @Override
    public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException
    {
        this.out.setNamespaceContext(context);
    }

    @Override
    public void setDefaultNamespace(final String uri) throws XMLStreamException
    {
        this.out.setDefaultNamespace(uri);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        this.out.writeStartDocument();
    }

    @Override
    public void writeStartDocument(final String version) throws XMLStreamException
    {
        this.out.writeStartDocument(version);
    }

    @Override
    public void writeStartDocument(final String encoding, final String version)
            throws XMLStreamException
    {
        this.out.writeStartDocument(encoding, version);
    }

    @Override
    public void writeDTD(final String dtd) throws XMLStreamException
    {
        this.out.writeDTD(dtd);
    }

    @Override
    public void writeProcessingInstruction(final String target) throws XMLStreamException
    {
        this.out.writeProcessingInstruction(target);
    }

    @Override
    public void writeProcessingInstruction(final String target, final String data)
            throws XMLStreamException
    {
        this.out.writeProcessingInstruction(target, data);
    }

    @Override
    public void writeComment(final String data) throws XMLStreamException
    {
        this.out.writeComment(data);
    }

    @Override
    public void writeEmptyElement(final String localName) throws XMLStreamException
    {
        this.out.writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(final String namespaceURI, final String localName)
            throws XMLStreamException
    {
        this.out.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(final String prefix, final String localName,
            final String namespaceURI) throws XMLStreamException
    {
        this.out.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeStartElement(final String localName) throws XMLStreamException
    {
        this.out.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(final String namespaceURI, final String localName)
            throws XMLStreamException
    {
        this.out.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(final String prefix, final String localName,
            final String namespaceURI) throws XMLStreamException
    {
        this.out.writeStartElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException
    {
        this.out.writeDefaultNamespace(namespaceURI);
    }

    @Override
    public void writeNamespace(final String prefix, final String namespaceURI)
            throws XMLStreamException
    {
        this.out.writeNamespace(prefix, namespaceURI);
    }

    @Override
    public String getPrefix(final String uri) throws XMLStreamException
    {
        return this.out.getPrefix(uri);
    }

    @Override
    public void setPrefix(final String prefix, final String uri) throws XMLStreamException
    {
        this.out.setPrefix(prefix, uri);
    }

    @Override
    public void writeAttribute(final String localName, final String value) throws XMLStreamException
    {
        this.out.writeAttribute(localName, value);
    }

    @Override
    public void writeAttribute(final String namespaceURI, final String localName,
            final String value) throws XMLStreamException
    {
        this.out.writeAttribute(namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(final String prefix, final String namespaceURI,
            final String localName, final String value) throws XMLStreamException
    {
        this.out.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeCharacters(final String text) throws XMLStreamException
    {
        this.out.writeCharacters(text);
    }

    @Override
    public void writeCharacters(final char[] text, final int start, final int len)
            throws XMLStreamException
    {
        this.out.writeCharacters(text, start, len);
    }

    @Override
    public void writeCData(final String data) throws XMLStreamException
    {
        this.out.writeCData(data);
    }

    @Override
    public void writeEntityRef(final String name) throws XMLStreamException
    {
        this.out.writeEntityRef(name);
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        this.out.writeEndElement();
    }

    @Override
    public void writeEndDocument() throws XMLStreamException
    {
        this.out.writeEndDocument();
    }

    @Override
    public void flush() throws XMLStreamException
    {
        this.out.flush();
    }

    @Override
    public void close() throws XMLStreamException
    {
        this.out.close();
    }
}
