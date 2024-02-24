package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

@ExtendWith(MockitoExtension.class)
class CallbackContentHandlerTest
{
    private static final String ELEMENT_URI = "";
    private static final String ELEMENT_LOCAL_NAME = "localName";
    private static final String ELEMENT_QNAME = "qname";

    @Mock
    Consumer<TreeElement> listenerMock;

    @Test
    void testSetDefaultStartElementListener()
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

    private TreeElement createElement()
    {
        return new TreeElement(createStartElement(), null);
    }

    private StartElementEvent createStartElement()
    {
        final Attributes attributesMock = mock(Attributes.class);
        when(attributesMock.getLength()).thenReturn(0);
        return StartElementEvent.create(ELEMENT_URI, ELEMENT_LOCAL_NAME, ELEMENT_QNAME,
                attributesMock, Location.create("path", 2));
    }

    private CallbackContentHandler testee()
    {
        return new CallbackContentHandler();
    }
}
