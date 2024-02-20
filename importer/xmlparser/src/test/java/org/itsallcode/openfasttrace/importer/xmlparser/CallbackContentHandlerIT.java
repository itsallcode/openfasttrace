package org.itsallcode.openfasttrace.importer.xmlparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javax.xml.namespace.QName;

import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.CallbackContentHandler;
import org.itsallcode.openfasttrace.importer.xmlparser.tree.TreeElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallbackContentHandlerIT {
    private static final String FILE_PATH = "file path";

    @Mock
    Consumer<TreeElement> elementListenerMock;
    @Mock
    Consumer<String> stringListenerMock;
    @Mock
    IntConsumer intListenerMock;

    CallbackContentHandler handler;

    @BeforeEach
    void setup() {
        this.handler = new CallbackContentHandler();
    }

    @Test
    void noListenerRegistered() {
        assertDoesNotThrow(() -> parse("<root attr=\"val\">charData<child/></root>"));
        verifyNoInteractions(elementListenerMock);
    }

    @Test
    void defaultListenerRegistered() {
        this.handler.setDefaultStartElementListener(elementListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        verify(elementListenerMock, times(2)).accept(any());
    }

    @Test
    void consumerFails() {
        this.handler.setDefaultStartElementListener(elementListenerMock);
        doThrow(new RuntimeException("expected")).when(elementListenerMock).accept(any());
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parse("<root attr=\"val\">charData<child/></root>"));
        assertThat(exception.getMessage(), allOf(
                Matchers.startsWith("Error handling TreeElement [element=StartElementEvent"),
                Matchers.endsWith("expected")));
    }

    @Test
    void startElementListenerReadsRootElement() {
        this.handler.addElementListener("root", elementListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        final TreeElement element = getCapturedElement();

        final QName name = element.getElement().getName();
        final Location location = element.getLocation();
        assertAll(() -> assertThat(name.getLocalPart(), equalTo("root")),
                () -> assertThat(name.getNamespaceURI(), equalTo("")),
                () -> assertThat(name.getPrefix(), equalTo("")),
                () -> assertThat(location.getPath(), equalTo(FILE_PATH)),
                () -> assertThat(location.getLine(), equalTo(1)),
                () -> assertThat(location.getColumn(), equalTo(18)),
                () -> assertThat(element.isRootElement(), is(true)),
                () -> assertThat(element.getCharacterData(), equalTo("charData")),
                () -> assertThat(element.getAttributeValueByName("attr").getValue(), equalTo("val")));
    }

    @Test
    void startElementListenerReadsChildElement() {
        this.handler.addElementListener("child", elementListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        final TreeElement element = getCapturedElement();

        final QName name = element.getElement().getName();
        final Location location = element.getLocation();
        assertAll(() -> assertThat(name.getLocalPart(), equalTo("child")),
                () -> assertThat(name.getNamespaceURI(), equalTo("")),
                () -> assertThat(name.getPrefix(), equalTo("")),
                () -> assertThat(location.getPath(), equalTo(FILE_PATH)),
                () -> assertThat(location.getLine(), equalTo(1)),
                () -> assertThat(location.getColumn(), equalTo(34)),
                () -> assertThat(element.isRootElement(), is(false)),
                () -> assertThat(element.getCharacterData(), equalTo("")),
                () -> assertThat(element.getAttributeValueByName("attr"), nullValue()));
    }

    @Test
    void endElementListenerReadsRootElement() {
        this.handler.addElementListener("root", null, elementListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        final TreeElement element = getCapturedElement();

        final QName name = element.getElement().getName();
        final Location location = element.getLocation();
        assertAll(() -> assertThat(name.getLocalPart(), equalTo("root")),
                () -> assertThat(name.getNamespaceURI(), equalTo("")),
                () -> assertThat(name.getPrefix(), equalTo("")),
                () -> assertThat(location.getPath(), equalTo(FILE_PATH)),
                () -> assertThat(location.getLine(), equalTo(1)),
                () -> assertThat(location.getColumn(), equalTo(18)),
                () -> assertThat(element.isRootElement(), is(true)),
                () -> assertThat(element.getCharacterData(), equalTo("charData")),
                () -> assertThat(element.getAttributeValueByName("attr").getValue(), equalTo("val")));
    }

    @Test
    void endElementListenerReadsChildElement() {
        this.handler.addElementListener("child", null, elementListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        final TreeElement element = getCapturedElement();

        final QName name = element.getElement().getName();
        final Location location = element.getLocation();
        assertAll(() -> assertThat(name.getLocalPart(), equalTo("child")),
                () -> assertThat(name.getNamespaceURI(), equalTo("")),
                () -> assertThat(name.getPrefix(), equalTo("")),
                () -> assertThat(location.getPath(), equalTo(FILE_PATH)),
                () -> assertThat(location.getLine(), equalTo(1)),
                () -> assertThat(location.getColumn(), equalTo(34)),
                () -> assertThat(element.isRootElement(), is(false)),
                () -> assertThat(element.getCharacterData(), equalTo("")),
                () -> assertThat(element.getAttributeValueByName("attr"), nullValue()));
    }

    @Test
    void addCharacterDataListener() {
        this.handler.addCharacterDataListener("root", stringListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        verify(stringListenerMock).accept("charData");
    }

    @Test
    void addCharacterDataListenerNoCharContent() {
        this.handler.addCharacterDataListener("child", stringListenerMock);
        parse("<root attr=\"val\">charData<child/></root>");
        verify(stringListenerMock).accept("");
    }

    @Test
    void addIntDataListener() {
        this.handler.addIntDataListener("root", intListenerMock);
        parse("<root>123<child/></root>");
        verify(intListenerMock).accept(123);
    }

    @Test
    void addIntDataListenerNoContent() {
        this.handler.addIntDataListener("root", intListenerMock);
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parse("<root><child/></root>"));
        assertThat(exception.getMessage(), equalTo("No string data found for element 'root'"));
    }

    @Test
    void addIntDataListenerStringContent() {
        this.handler.addIntDataListener("root", intListenerMock);
        final XmlParserException exception = assertThrows(XmlParserException.class,
                () -> parse("<root>invalidInt<child/></root>"));
        assertThat(exception.getMessage(), equalTo("Failed parsing content 'invalidInt' of element 'root'"));
    }

    private TreeElement getCapturedElement() {
        final ArgumentCaptor<TreeElement> arg = ArgumentCaptor.forClass(TreeElement.class);
        verify(elementListenerMock).accept(arg.capture());
        return arg.getValue();
    }

    private void parse(final String content) {
        new XmlParserFactory().createParser().parse(FILE_PATH, new StringReader(content), handler);
    }
}
