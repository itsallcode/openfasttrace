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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Deque;
import java.util.LinkedList;

/**
 * An {@link XMLStreamWriter} that wraps another {@link XMLStreamWriter} and
 * indents the output.
 */
public class IndentingXMLStreamWriter extends DelegatingXMLStreamWriter implements AutoCloseable
{
    private enum State
    {
        SEEN_NOTHING, SEEN_ELEMENT, SEEN_DATA
    }

    private static final String NEWLINE = "\n";
    private static final String INDENTATION = "  ";
    private final Deque<State> stateStack = new LinkedList<>();

    private State state = State.SEEN_NOTHING;

    private int depth = 0;

    /**
     * Create an new instance wrapping a delegate.
     * 
     * @param writer
     *            the delegate writer.
     */
    public IndentingXMLStreamWriter(XMLStreamWriter writer)
    {
        super(writer);
    }

    @Override
    public void writeStartDocument() throws XMLStreamException
    {
        super.writeStartDocument();
        super.writeCharacters(NEWLINE);
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException
    {
        super.writeStartDocument(version);
        super.writeCharacters(NEWLINE);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException
    {
        super.writeStartDocument(encoding, version);
        super.writeCharacters(NEWLINE);
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException
    {
        onStartElement();
        super.writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException
    {
        onStartElement();
        super.writeStartElement(namespaceURI, localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException
    {
        onStartElement();
        super.writeStartElement(prefix, localName, namespaceURI);
    }

    private void onStartElement() throws XMLStreamException
    {
        stateStack.push(State.SEEN_ELEMENT);
        state = State.SEEN_NOTHING;
        if (depth > 0)
        {
            super.writeCharacters(NEWLINE);
        }
        doIndent();
        depth++;
    }

    private void doIndent() throws XMLStreamException
    {
        for (int i = 0; i < depth; i++)
        {
            super.writeCharacters(INDENTATION);
        }
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException
    {
        onEmptyElement();
        super.writeEmptyElement(namespaceURI, localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException
    {
        onEmptyElement();
        super.writeEmptyElement(prefix, localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException
    {
        onEmptyElement();
        super.writeEmptyElement(localName);
    }

    private void onEmptyElement() throws XMLStreamException
    {
        state = State.SEEN_ELEMENT;
        if (depth > 0)
        {
            super.writeCharacters(NEWLINE);
        }
        doIndent();
    }

    @Override
    public void writeEndElement() throws XMLStreamException
    {
        onEndElement();
        super.writeEndElement();
    }

    @Override
    public void close() throws XMLStreamException
    {
        super.close();
    }

    private void onEndElement() throws XMLStreamException
    {
        depth--;
        if (state == State.SEEN_ELEMENT)
        {
            super.writeCharacters(NEWLINE);
            doIndent();
        }
        if (stateStack.isEmpty())
        {
            state = null;
        }
        else
        {
            state = stateStack.pop();
        }
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException
    {
        state = State.SEEN_DATA;
        super.writeCharacters(text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException
    {
        state = State.SEEN_DATA;
        super.writeCharacters(text, start, len);
    }

    @Override
    public void writeCData(String data) throws XMLStreamException
    {
        state = State.SEEN_DATA;
        super.writeCData(data);
    }

}
