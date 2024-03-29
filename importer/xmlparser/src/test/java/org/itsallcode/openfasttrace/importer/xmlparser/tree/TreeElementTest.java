package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

@ExtendWith(MockitoExtension.class)
class TreeElementTest
{
    private static final String ELEMENT_URI = "uri";
    private static final String ELEMENT_LOCAL_NAME = "localName";
    private static final String ELEMENT_QNAME = "qname";

    @Mock
    Consumer<TreeElement> listenerMock1;
    @Mock
    Consumer<TreeElement> listenerMock2;

    @Test
    void testToString()
    {
        assertThat(testee().toString(), equalTo(
                "TreeElement [element=StartElementEvent [qName={uri}localName, attributeMap={qname0=Attribute [qName=qname0, value=value0]}, location=path:2], characterData=, endElementListeners=[], parent=null]"));
    }

    @Test
    void testGetElement()
    {
        assertThat(testee().getElement().getName().getLocalPart(), equalTo(ELEMENT_LOCAL_NAME));
    }

    @Test
    void testGetCharacterDataEmpty()
    {
        assertThat(testee().getCharacterData(), equalTo(""));
    }

    @Test
    void testGetCharacterData()
    {
        final TreeElement element = testee();
        element.addCharacterData("chars");
        assertThat(element.getCharacterData(), equalTo("chars"));
    }

    @Test
    void testGetCharacterDataMultipleAdded()
    {
        final TreeElement element = testee();
        element.addCharacterData("chars");
        element.addCharacterData(", more chars");
        assertThat(element.getCharacterData(), equalTo("chars, more chars"));
    }

    @Test
    void testIsRootTrueForNullParent()
    {
        assertThat(testee().isRootElement(), is(true));
    }

    @Test
    void testIsRootFalsForNonNullParent()
    {
        final TreeElement element = new TreeElement(createStartElement(), mock(TreeElement.class));
        assertThat(element.isRootElement(), is(false));
    }

    @Test
    void testGetAttributeValueByName()
    {
        assertThat(testee().getAttributeValueByName("qname0").getValue(), equalTo("value0"));
    }

    @Test
    void testGetLocation()
    {
        assertThat(testee().getLocation().getPath(), equalTo("path"));
    }

    @Test
    void testInvokeEndElementListenersSuccedsForEmptyList()
    {
        assertDoesNotThrow(testee()::invokeEndElementListeners);
    }

    @Test
    void testInvokeEndElementListenersCallsSingleEntry()
    {
        final TreeElement element = testee();
        element.addEndElementListener(listenerMock1);
        element.invokeEndElementListeners();
        verify(listenerMock1).accept(same(element));
    }

    @Test
    void testInvokeEndElementListenersCallsMultipleEntries()
    {
        final TreeElement element = testee();
        element.addEndElementListener(listenerMock1);
        element.addEndElementListener(listenerMock2);
        element.invokeEndElementListeners();
        final InOrder inOrder = inOrder(listenerMock1, listenerMock2);
        inOrder.verify(listenerMock1).accept(same(element));
        inOrder.verify(listenerMock2).accept(same(element));
        inOrder.verifyNoMoreInteractions();
    }

    private TreeElement testee()
    {
        return new TreeElement(createStartElement(), null);
    }

    private StartElementEvent createStartElement()
    {
        final Attributes attributesMock = mock(Attributes.class);
        when(attributesMock.getLength()).thenReturn(1);
        when(attributesMock.getQName(0)).thenReturn("qname0");
        when(attributesMock.getValue(0)).thenReturn("value0");
        return StartElementEvent.create(ELEMENT_URI, ELEMENT_LOCAL_NAME, ELEMENT_QNAME,
                attributesMock, Location.create("path", 2));
    }
}
