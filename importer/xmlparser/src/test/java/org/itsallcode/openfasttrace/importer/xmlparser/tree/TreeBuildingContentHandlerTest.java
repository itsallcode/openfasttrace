package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import org.itsallcode.openfasttrace.importer.xmlparser.ContentHandlerAdapterController;
import org.itsallcode.openfasttrace.importer.xmlparser.event.EndElementEvent;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

@ExtendWith(MockitoExtension.class)
class TreeBuildingContentHandlerTest
{
    @Mock
    TreeContentHandler handlerMock;

    @Test
    void testConstructorFailsForNullHandler()
    {
        assertThrows(NullPointerException.class, () -> new TreeBuildingContentHandler(null));
    }

    @Test
    void testInitCallsDelegate(@Mock final ContentHandlerAdapterController adapterMock)
    {
        final TreeBuildingContentHandler testee = testee();
        testee.init(adapterMock);
        verify(handlerMock).init(same(testee));
    }

    @Test
    void testStopParsingCallsHandler(@Mock final ContentHandlerAdapterController adapterMock)
    {
        final TreeBuildingContentHandler testee = testee();
        testee.init(adapterMock);
        testee.stopParsing();
        verify(adapterMock).parsingFinished();
    }

    @Test
    void testStopParsingFailsWithoutInit()
    {
        final TreeBuildingContentHandler testee = testee();
        assertThrows(NullPointerException.class, testee::stopParsing);
    }

    @Test
    void testStopParsingFailsWithNullAdapter()
    {
        final TreeBuildingContentHandler testee = testee();
        testee.init(null);
        assertThrows(NullPointerException.class, testee::stopParsing);
    }

    @Test
    void testSetDelegateCallsInit(@Mock final TreeContentHandler newDelegateMock)
    {
        final TreeBuildingContentHandler testee = testee();
        testee.setDelegate(newDelegateMock);
        verify(newDelegateMock).init(same(testee));
        verifyNoInteractions(handlerMock);
    }

    @Test
    void testSetDelegateToNullFails()
    {
        final TreeBuildingContentHandler testee = testee();
        assertThrows(NullPointerException.class, () -> testee.setDelegate(null));
    }

    @Test
    void testGetCurrentElementFailsWithEmptyStack()
    {
        final TreeBuildingContentHandler testee = testee();
        final IllegalStateException exception = assertThrows(IllegalStateException.class, testee::getCurrentElement);
        assertThat(exception.getMessage(), equalTo("Stack is empty"));
    }

    @Test
    void testGetCurrentElementReturnsRootElement()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        testee.startElement(rootElement);
        final TreeElement currentElement = testee.getCurrentElement();
        assertAll(() -> assertThat(currentElement.getElement(), sameInstance(rootElement)),
                () -> assertThat(currentElement.isRootElement(), is(true)));
    }

    @Test
    void testGetCurrentElementHasNoCharacterDataByDefault()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        testee.startElement(rootElement);
        final TreeElement currentElement = testee.getCurrentElement();
        assertThat(currentElement.getCharacterData(), equalTo(""));
    }

    @Test
    void testGetCurrentElementReturnsChildElement()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        final StartElementEvent childElement = createStartElement("child");
        testee.startElement(rootElement);
        testee.startElement(childElement);
        final TreeElement currentElement = testee.getCurrentElement();
        assertAll(() -> assertThat(currentElement.getElement(), sameInstance(childElement)),
                () -> assertThat(currentElement.isRootElement(), is(false)));
    }

    @Test
    void testStartElementCallsDelegate()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        testee.startElement(rootElement);

        final ArgumentCaptor<TreeElement> arg = ArgumentCaptor.forClass(TreeElement.class);
        verify(handlerMock).startElement(arg.capture());
        assertThat(arg.getValue().getElement(), sameInstance(rootElement));
    }

    @Test
    void testEndElementFailsForEmptyStack()
    {
        final TreeBuildingContentHandler testee = testee();
        final EndElementEvent endElement = createEndElement("root");
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> testee.endElement(endElement));
        assertThat(exception.getMessage(),
                equalTo("Got closing event EndElementEvent [qName={uri}root, location=null] but stack is empty"));
    }

    @Test
    void testEndElementFailsForWrongElement()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        final EndElementEvent endElement = createEndElement("wrong");
        testee.startElement(rootElement);
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> testee.endElement(endElement));
        assertThat(exception.getMessage(),
                equalTo("Top stack element is StartElementEvent [qName={uri}root, attributeMap={}, location=null] but got end event for EndElementEvent [qName={uri}wrong, location=null]"));
    }

    @Test
    void testEndElementCallsDelegate()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        final EndElementEvent endElement = createEndElement("root");
        testee.startElement(rootElement);
        testee.endElement(endElement);

        final ArgumentCaptor<TreeElement> arg = ArgumentCaptor.forClass(TreeElement.class);
        verify(handlerMock).endElement(arg.capture());
        assertThat(arg.getValue().getElement(), sameInstance(rootElement));
    }

    @Test
    void testEndElementPopsStack()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        final EndElementEvent endElement = createEndElement("root");
        testee.startElement(rootElement);
        testee.endElement(endElement);

        final IllegalStateException exception = assertThrows(IllegalStateException.class, testee::getCurrentElement);
        assertThat(exception.getMessage(), equalTo("Stack is empty"));
    }

    @Test
    void testCharactersFailsForEmptyStack()
    {
        final TreeBuildingContentHandler testee = testee();
        final IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> testee.characters("chars"));
        assertThat(exception.getMessage(), equalTo("Got characters 'chars' but stack is empty"));
    }

    @Test
    void testCharactersUpdatesCurrentElement()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        testee.startElement(rootElement);
        testee.characters("chars");
        assertThat(testee.getCurrentElement().getCharacterData(), equalTo("chars"));
    }

    @Test
    void testCharactersAddsMoreCharacters()
    {
        final TreeBuildingContentHandler testee = testee();
        final StartElementEvent rootElement = createStartElement("root");
        testee.startElement(rootElement);
        testee.characters("chars1");
        testee.characters("chars2");
        assertThat(testee.getCurrentElement().getCharacterData(), equalTo("chars1chars2"));
    }

    private StartElementEvent createStartElement(final String name)
    {
        return StartElementEvent.create("uri", name, "qname", mock(Attributes.class), null);
    }

    private EndElementEvent createEndElement(final String name)
    {
        return EndElementEvent.create("uri", name, "qname", null);
    }

    private TreeBuildingContentHandler testee()
    {
        return new TreeBuildingContentHandler(handlerMock);
    }

}
