package org.itsallcode.openfasttrace.importer.xmlparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import org.itsallcode.openfasttrace.importer.xmlparser.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.*;

@ExtendWith(MockitoExtension.class)
class ContentHandlerAdapterTest
{
    @Mock
    XMLReader xmlReaderMock;
    @Mock
    EventContentHandler delegateMock;

    @Test
    void testRegisterListener()
    {
        final ContentHandlerAdapter testee = testee();
        testee.registerListener();
        verify(delegateMock).init(same(testee));
        verify(xmlReaderMock).setContentHandler(same(testee));
    }

    @Test
    void testRegisterListenerFailsWhenContentHandlerAlreadyRegistered()
    {
        final ContentHandlerAdapter testee = testee();
        final ContentHandler contentHandlerMock = mock(ContentHandler.class);
        when(xmlReaderMock.getContentHandler()).thenReturn(contentHandlerMock);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, testee::registerListener);
        assertThat(exception.getMessage(),
                equalTo("An XML content handler is already registered: " + contentHandlerMock.toString()));
    }

    @Test
    void testSetDocumentLocatorFailsForNullValue()
    {
        final ContentHandlerAdapter testee = testee();
        assertThrows(NullPointerException.class, () -> testee.setDocumentLocator(null));
    }

    @Test
    void testStartElementFailsForMissingLocator() throws SAXException
    {
        final ContentHandlerAdapter testee = testee();
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> testee.startElement("uri", "localName", "qName", null));
        assertThat(exception.getMessage(), equalTo("Document locator was not defined"));
    }

    @Test
    void testStartElementCallsDelegate() throws SAXException
    {
        final ContentHandlerAdapter testee = testee();
        testee.setDocumentLocator(createLocator(42, 17));
        testee.startElement("uri", "localName", "qName", mock(Attributes.class));

        final ArgumentCaptor<StartElementEvent> arg = ArgumentCaptor.forClass(StartElementEvent.class);
        verify(delegateMock).startElement(arg.capture());
        final StartElementEvent event = arg.getValue();
        assertAll(() -> assertThat(event.getName().toString(), equalTo("{uri}localName")),
                () -> assertThat(event.getLocation().getPath(), equalTo("file")),
                () -> assertThat(event.getLocation().getLine(), equalTo(42)),
                () -> assertThat(event.getLocation().getColumn(), equalTo(17)));
    }

    @Test
    void testEndElementFailsForMissingLocator() throws SAXException
    {
        final ContentHandlerAdapter testee = testee();
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> testee.endElement("uri", "localName", "qName"));
        assertThat(exception.getMessage(), equalTo("Document locator was not defined"));
    }

    @Test
    void testEndElementCallsDelegate() throws SAXException
    {
        final ContentHandlerAdapter testee = testee();
        testee.setDocumentLocator(createLocator(42, 17));
        testee.endElement("uri", "localName", "qName");

        final ArgumentCaptor<EndElementEvent> arg = ArgumentCaptor.forClass(EndElementEvent.class);
        verify(delegateMock).endElement(arg.capture());
        final EndElementEvent event = arg.getValue();
        assertAll(() -> assertThat(event.getName().toString(), equalTo("{uri}localName")),
                () -> assertThat(event.getLocation().getPath(), equalTo("file")),
                () -> assertThat(event.getLocation().getLine(), equalTo(42)),
                () -> assertThat(event.getLocation().getColumn(), equalTo(17)));
    }

    @Test
    void testCharactersCallsDelegate()
    {
        final ContentHandlerAdapter testee = testee();
        testee.characters(new char[] { 'a', 'b', 'c' }, 0, 3);
        verify(delegateMock).characters("abc");
    }

    @Test
    void testParsingFinishedSetsNullContentHandler()
    {
        final ContentHandlerAdapter testee = testee();
        testee.parsingFinished();
        verify(xmlReaderMock).setContentHandler(null);
    }

    private Locator createLocator(final int lineNumber, final int columnNumber)
    {
        final Locator mock = mock(Locator.class);
        when(mock.getLineNumber()).thenReturn(lineNumber);
        when(mock.getColumnNumber()).thenReturn(columnNumber);
        return mock;
    }

    private ContentHandlerAdapter testee()
    {
        return new ContentHandlerAdapter("file", xmlReaderMock, delegateMock);
    }
}
