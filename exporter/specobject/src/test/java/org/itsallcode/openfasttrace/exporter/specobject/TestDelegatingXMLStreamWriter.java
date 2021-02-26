package org.itsallcode.openfasttrace.exporter.specobject;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestDelegatingXMLStreamWriter
{
    private static final String LOCAL_NAME = "local name";
    private static final String NAMESPACE_URI = "namespace uri";
    private static final String PREFIX = "prefix";
    private static final String ATTRIBUTE_VALUE = "attr value";
    private static final String DATA = "data";
    private static final String PI_TARGET = "processing instruction target";
    private static final String NAME = "name";
    private static final String DOC_VERSION = "doc version";
    private static final String ENCODING = "encoding";
    private static final int LENGTH = 20;
    private static final int START = 10;
    private static final char[] TEXT = new char[] { 't', 'e', 'x', 't' };
    private static final String PROPERTY_NAME = "property name";
    private static final Object PROPERTY_VALUE = "property value";

    @Mock
    private NamespaceContext contextMock;
    @Mock
    private XMLStreamWriter delegateMock;

    private DelegatingXMLStreamWriter writer;

    @BeforeEach
    void setUp()
    {
        writer = new DelegatingXMLStreamWriter(delegateMock);
    }

    @Test
    void testWriteStartElementString() throws XMLStreamException
    {
        writer.writeStartElement(LOCAL_NAME);
        verify(delegateMock).writeStartElement(LOCAL_NAME);
    }

    @Test
    void testWriteStartElementStringString() throws XMLStreamException
    {
        writer.writeStartElement(NAMESPACE_URI, LOCAL_NAME);
        verify(delegateMock).writeStartElement(NAMESPACE_URI, LOCAL_NAME);
    }

    @Test
    void testWriteStartElementStringStringString() throws XMLStreamException
    {
        writer.writeStartElement(PREFIX, NAMESPACE_URI, LOCAL_NAME);
        verify(delegateMock).writeStartElement(PREFIX, NAMESPACE_URI, LOCAL_NAME);
    }

    @Test
    void testWriteEmptyElementStringString() throws XMLStreamException
    {
        writer.writeEmptyElement(NAMESPACE_URI, LOCAL_NAME);
        verify(delegateMock).writeEmptyElement(NAMESPACE_URI, LOCAL_NAME);
    }

    @Test
    void testWriteEmptyElementStringStringString() throws XMLStreamException
    {
        writer.writeEmptyElement(PREFIX, NAMESPACE_URI, LOCAL_NAME);
        verify(delegateMock).writeEmptyElement(PREFIX, NAMESPACE_URI, LOCAL_NAME);
    }

    @Test
    void testWriteEmptyElementString() throws XMLStreamException
    {
        writer.writeEmptyElement(LOCAL_NAME);
        verify(delegateMock).writeEmptyElement(LOCAL_NAME);
    }

    @Test
    void testWriteEndElement() throws XMLStreamException
    {
        writer.writeEndElement();
        verify(delegateMock).writeEndElement();
    }

    @Test
    void testWriteEndDocument() throws XMLStreamException
    {
        writer.writeEndDocument();
        verify(delegateMock).writeEndDocument();
    }

    @Test
    void testClose() throws XMLStreamException
    {
        writer.close();
        verify(delegateMock).close();
    }

    @Test
    void testFlush() throws XMLStreamException
    {
        writer.flush();
        verify(delegateMock).flush();
    }

    @Test
    void testWriteAttributeStringString() throws XMLStreamException
    {
        writer.writeAttribute(LOCAL_NAME, ATTRIBUTE_VALUE);
        verify(delegateMock).writeAttribute(LOCAL_NAME, ATTRIBUTE_VALUE);
    }

    @Test
    void testWriteAttributeStringStringStringString() throws XMLStreamException
    {
        writer.writeAttribute(PREFIX, NAMESPACE_URI, LOCAL_NAME, ATTRIBUTE_VALUE);
        verify(delegateMock).writeAttribute(PREFIX, NAMESPACE_URI, LOCAL_NAME, ATTRIBUTE_VALUE);
    }

    @Test
    void testWriteAttributeStringStringString() throws XMLStreamException
    {
        writer.writeAttribute(NAMESPACE_URI, LOCAL_NAME, ATTRIBUTE_VALUE);
        verify(delegateMock).writeAttribute(NAMESPACE_URI, LOCAL_NAME, ATTRIBUTE_VALUE);
    }

    @Test
    void testWriteNamespace() throws XMLStreamException
    {
        writer.writeNamespace(PREFIX, NAMESPACE_URI);
        verify(delegateMock).writeNamespace(PREFIX, NAMESPACE_URI);
    }

    @Test
    void testWriteDefaultNamespace() throws XMLStreamException
    {
        writer.writeDefaultNamespace(NAMESPACE_URI);
        verify(delegateMock).writeDefaultNamespace(NAMESPACE_URI);
    }

    @Test
    void testWriteComment() throws XMLStreamException
    {
        writer.writeComment(DATA);
        verify(delegateMock).writeComment(DATA);
    }

    @Test
    void testWriteProcessingInstructionString() throws XMLStreamException
    {
        writer.writeProcessingInstruction(PI_TARGET);
        verify(delegateMock).writeProcessingInstruction(PI_TARGET);
    }

    @Test
    void testWriteProcessingInstructionStringString() throws XMLStreamException
    {
        writer.writeProcessingInstruction(PI_TARGET, DATA);
        verify(delegateMock).writeProcessingInstruction(PI_TARGET, DATA);
    }

    @Test
    void testWriteCData() throws XMLStreamException
    {
        writer.writeCData(DATA);
        verify(delegateMock).writeCData(DATA);
    }

    @Test
    void testWriteDTD() throws XMLStreamException
    {
        writer.writeDTD(DATA);
        verify(delegateMock).writeDTD(DATA);
    }

    @Test
    void testWriteEntityRef() throws XMLStreamException
    {
        writer.writeEntityRef(NAME);
        verify(delegateMock).writeEntityRef(NAME);
    }

    @Test
    void testWriteStartDocument() throws XMLStreamException
    {
        writer.writeStartDocument();
        verify(delegateMock).writeStartDocument();
    }

    @Test
    void testWriteStartDocumentString() throws XMLStreamException
    {
        writer.writeStartDocument(DOC_VERSION);
        verify(delegateMock).writeStartDocument(DOC_VERSION);
    }

    @Test
    void testWriteStartDocumentStringString() throws XMLStreamException
    {
        writer.writeStartDocument(ENCODING, DOC_VERSION);
        verify(delegateMock).writeStartDocument(ENCODING, DOC_VERSION);
    }

    @Test
    void testWriteCharactersString() throws XMLStreamException
    {
        writer.writeCharacters(DATA);
        verify(delegateMock).writeCharacters(DATA);
    }

    @Test
    void testWriteCharactersCharArrayIntInt() throws XMLStreamException
    {
        writer.writeCharacters(TEXT, START, LENGTH);
        verify(delegateMock).writeCharacters(TEXT, START, LENGTH);
    }

    @Test
    void testGetPrefix() throws XMLStreamException
    {
        when(delegateMock.getPrefix(NAMESPACE_URI)).thenReturn(PREFIX);
        assertThat(writer.getPrefix(NAMESPACE_URI), equalTo(PREFIX));
        verify(delegateMock).getPrefix(NAMESPACE_URI);
    }

    @Test
    void testSetPrefix() throws XMLStreamException
    {
        writer.setPrefix(PREFIX, NAMESPACE_URI);
        verify(delegateMock).setPrefix(PREFIX, NAMESPACE_URI);
    }

    @Test
    void testSetDefaultNamespace() throws XMLStreamException
    {
        writer.setDefaultNamespace(NAMESPACE_URI);
        verify(delegateMock).setDefaultNamespace(NAMESPACE_URI);
    }

    @Test
    void testSetNamespaceContext() throws XMLStreamException
    {
        writer.setNamespaceContext(contextMock);
        verify(delegateMock).setNamespaceContext(same(contextMock));
    }

    @Test
    void testGetNamespaceContext()
    {
        when(delegateMock.getNamespaceContext()).thenReturn(contextMock);
        assertThat(writer.getNamespaceContext(), sameInstance(contextMock));
        verify(delegateMock).getNamespaceContext();
    }

    @Test
    void testGetProperty()
    {
        when(delegateMock.getProperty(PROPERTY_NAME)).thenReturn(PROPERTY_VALUE);
        assertThat(writer.getProperty(PROPERTY_NAME), equalTo(PROPERTY_VALUE));
        verify(delegateMock).getProperty(PROPERTY_NAME);
    }
}
