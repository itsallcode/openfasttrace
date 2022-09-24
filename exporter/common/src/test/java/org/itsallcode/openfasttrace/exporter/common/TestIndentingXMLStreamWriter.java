package org.itsallcode.openfasttrace.exporter.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class TestIndentingXMLStreamWriter
{
    private static final String LOCAL_NAME = "local name";
    private static final String NAMESPACE_URI = "namespace uri";
    private static final String DATA = "data";
    private static final String VERSION = "version";
    private static final String ENCODING = "ecoding";

    private static final String NEWLINE = "\n";

    @Mock
    private XMLStreamWriter delegateMock;

    private IndentingXMLStreamWriter writer;

    private InOrder delegateInOrder;

    @BeforeEach
    void setUp()
    {
        writer = new IndentingXMLStreamWriter(delegateMock);
        delegateInOrder = inOrder(delegateMock);
    }

    @Test
    void testWriteStartDocument() throws XMLStreamException
    {
        writer.writeStartDocument();
        delegateInOrder.verify(delegateMock).writeStartDocument();
        delegateInOrder.verify(delegateMock).writeCharacters(NEWLINE);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartDocumentString() throws XMLStreamException
    {
        writer.writeStartDocument(VERSION);
        delegateInOrder.verify(delegateMock).writeStartDocument(VERSION);
        delegateInOrder.verify(delegateMock).writeCharacters(NEWLINE);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartElementString() throws XMLStreamException
    {
        writer.writeStartElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeStartElement(LOCAL_NAME);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartElementStringDepthGreaterThanZero() throws XMLStreamException
    {
        writer.writeStartElement(LOCAL_NAME);
        writer.writeStartElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeStartElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeCharacters("\n");
        delegateInOrder.verify(delegateMock).writeCharacters("  ");
        delegateInOrder.verify(delegateMock).writeStartElement(LOCAL_NAME);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartElementStringString() throws XMLStreamException
    {
        writer.writeStartElement(NAMESPACE_URI, LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeStartElement(NAMESPACE_URI, LOCAL_NAME);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartElementStringStringString() throws XMLStreamException
    {
        writer.writeStartElement(NAMESPACE_URI, LOCAL_NAME, NAMESPACE_URI);
        delegateInOrder.verify(delegateMock).writeStartElement(NAMESPACE_URI, LOCAL_NAME,
                NAMESPACE_URI);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEmptyElementStringString() throws XMLStreamException
    {
        writer.writeEmptyElement(NAMESPACE_URI, LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeEmptyElement(NAMESPACE_URI, LOCAL_NAME);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEmptyElementStringStringString() throws XMLStreamException
    {
        writer.writeEmptyElement(NAMESPACE_URI, LOCAL_NAME, NAMESPACE_URI);
        delegateInOrder.verify(delegateMock).writeEmptyElement(NAMESPACE_URI, LOCAL_NAME,
                NAMESPACE_URI);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEmptyElementString() throws XMLStreamException
    {
        writer.writeEmptyElement(NAMESPACE_URI);
        delegateInOrder.verify(delegateMock).writeEmptyElement(NAMESPACE_URI);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEmptyElementStringDepthGreaterThanZero() throws XMLStreamException
    {
        writer.writeStartElement(LOCAL_NAME);
        writer.writeEmptyElement(NAMESPACE_URI);
        delegateInOrder.verify(delegateMock).writeStartElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeCharacters("\n");
        delegateInOrder.verify(delegateMock).writeCharacters("  ");
        delegateInOrder.verify(delegateMock).writeEmptyElement(NAMESPACE_URI);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEndElement() throws XMLStreamException
    {
        writer.writeEndElement();
        delegateInOrder.verify(delegateMock).writeEndElement();
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteEndElementDepthGreaterThanZero() throws XMLStreamException
    {
        writer.writeStartElement(LOCAL_NAME);
        writer.writeEmptyElement(LOCAL_NAME);
        writer.writeEndElement();
        delegateInOrder.verify(delegateMock).writeStartElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeCharacters("\n");
        delegateInOrder.verify(delegateMock).writeCharacters("  ");
        delegateInOrder.verify(delegateMock).writeEmptyElement(LOCAL_NAME);
        delegateInOrder.verify(delegateMock).writeCharacters("\n");
        // delegateInOrder.verify(delegateMock).writeCharacters(" ");
        delegateInOrder.verify(delegateMock).writeEndElement();
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteCData() throws XMLStreamException
    {
        writer.writeCData(DATA);
        delegateInOrder.verify(delegateMock).writeCData(DATA);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteStartDocumentStringString() throws XMLStreamException
    {
        writer.writeStartDocument(ENCODING, VERSION);
        delegateInOrder.verify(delegateMock).writeStartDocument(ENCODING, VERSION);
        delegateInOrder.verify(delegateMock).writeCharacters("\n");
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteCharactersString() throws XMLStreamException
    {
        writer.writeCharacters(DATA);
        delegateInOrder.verify(delegateMock).writeCharacters(DATA);
        delegateInOrder.verifyNoMoreInteractions();
    }

    @Test
    void testWriteCharactersCharArrayIntInt() throws XMLStreamException
    {
        final char[] text = { 'a', 'b', 'c' };
        final int start = 10;
        final int len = 20;
        writer.writeCharacters(text, start, len);
        delegateInOrder.verify(delegateMock).writeCharacters(text, start, len);
        delegateInOrder.verifyNoMoreInteractions();
    }
}
