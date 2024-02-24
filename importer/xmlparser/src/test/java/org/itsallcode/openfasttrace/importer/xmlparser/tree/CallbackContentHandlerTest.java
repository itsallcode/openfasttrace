package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

@ExtendWith(MockitoExtension.class)
class CallbackContentHandlerTest
{
    private static final String ELEMENT_URI = "";
    private static final String ELEMENT_LOCAL_NAME = "localName";
    private static final String ELEMENT_QNAME = "qname";

    @Test
    void testSetDefaultStartElementListener(@Mock final Consumer<TreeElement> listenerMock)
    {
        final CallbackContentHandler handler = testee();
        handler.setDefaultStartElementListener(listenerMock);
        final TreeElement element = createElement();
        handler.startElement(element);
        verify(listenerMock).accept(same(element));
    }

    @Test
    void testSetNullDefaultStartElementListenerIgnoresEvent()
    {
        final CallbackContentHandler handler = testee();
        handler.setDefaultStartElementListener(null);
        assertDoesNotThrow(() -> handler.startElement(createElement()));
    }

    @Test
    void testAddElementListenerFailsForDuplicateElementName(@Mock final Consumer<TreeElement> listenerMock)
    {
        final CallbackContentHandler handler = testee();
        handler.addElementListener("element", listenerMock, listenerMock);
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> handler.addElementListener("element", listenerMock, listenerMock));
        assertThat(exception.getMessage(), equalTo("Listener already registered for start element 'element'"));
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "https://github.com/itsallcode/openfasttrace", "" })
    void testStartElementCallsListener(final String namespaceUri, @Mock final Consumer<TreeElement> listenerMock)
    {
        final CallbackContentHandler handler = testee();
        handler.setDefaultStartElementListener(listenerMock);
        final TreeElement element = createElement(createStartElement(namespaceUri));
        handler.startElement(element);
        verify(listenerMock).accept(element);
    }

    @ParameterizedTest
    @ValueSource(strings =
    { "nonEmpty", "https://github.com/itsallcode/openfasttrace/custom" })
    void testStartElementIgnoresNamespaceElements(final String namespaceUri,
            @Mock final Consumer<TreeElement> listenerMock)
    {
        final CallbackContentHandler handler = testee();
        handler.setDefaultStartElementListener(listenerMock);
        final TreeElement element = createElement(createStartElement(namespaceUri));
        handler.startElement(element);
        verify(listenerMock, never()).accept(any());
    }

    @Test
    void testStopParsing(@Mock final TreeParsingController controllerMock)
    {
        final CallbackContentHandler handler = testee();
        handler.init(controllerMock);
        handler.stopParsing();
        verify(controllerMock).stopParsing();
    }

    private TreeElement createElement()
    {
        return createElement(createStartElement());
    }

    private TreeElement createElement(final StartElementEvent startElement)
    {
        return new TreeElement(startElement, null);

    }

    private StartElementEvent createStartElement()
    {
        return createStartElement(ELEMENT_URI);
    }

    private StartElementEvent createStartElement(final String namespaceUri)
    {
        final Attributes attributesMock = mock(Attributes.class);
        when(attributesMock.getLength()).thenReturn(0);
        return StartElementEvent.create(namespaceUri, ELEMENT_LOCAL_NAME, ELEMENT_QNAME,
                attributesMock, Location.create("path", 2));
    }

    private CallbackContentHandler testee()
    {
        return new CallbackContentHandler();
    }
}
