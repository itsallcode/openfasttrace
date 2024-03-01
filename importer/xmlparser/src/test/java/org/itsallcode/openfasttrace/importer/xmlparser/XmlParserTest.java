package org.itsallcode.openfasttrace.importer.xmlparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeContentHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.SAXException;

@ExtendWith(MockitoExtension.class)
class XmlParserTest
{
    @Test
    void testParsingSucceeds(@Mock final TreeContentHandler handlerMock) throws IOException
    {
        final XmlParser parser = testee();
        parser.parse("path", new StringReader("<root/>"), handlerMock);
        verify(handlerMock).startElement(any());
    }

    @Test
    void testParsingInvalidXmlFormatFails(@Mock final TreeContentHandler handlerMock) throws IOException
    {
        final XmlParser parser = testee();
        final StringReader reader = new StringReader("invalidContent");
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parser.parse("path", reader, handlerMock));
        assertThat(exception.getMessage(), equalTo("Failed to parse file 'path': Content is not allowed in prolog."));
    }

    @Test
    void testParsingFails(@Mock final Reader readerMock, @Mock final TreeContentHandler handlerMock) throws IOException
    {
        final XmlParser parser = testee();
        when(readerMock.read(any(), anyInt(), anyInt())).thenThrow(new IOException("expected"));
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parser.parse("path", readerMock, handlerMock));
        assertThat(exception.getMessage(), equalTo("Failed to parse file 'path': expected"));
    }

    @Test
    void testCreateXmlReaderFails(@Mock final SAXParserFactory parserFactoryMock)
            throws ParserConfigurationException, SAXException
    {
        final XmlParser parser = new XmlParser(parserFactoryMock);
        when(parserFactoryMock.newSAXParser()).thenThrow(new SAXException("expected"));
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parser.parse("path", null, null));
        assertThat(exception.getMessage(), equalTo("Failed to create XML reader: expected"));
    }

    private XmlParser testee()
    {
        return new XmlParserFactory().createParser();
    }
}
