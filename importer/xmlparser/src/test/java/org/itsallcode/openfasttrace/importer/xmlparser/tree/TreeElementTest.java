package org.itsallcode.openfasttrace.importer.xmlparser.tree;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.function.Consumer;

import org.itsallcode.openfasttrace.api.core.Location;
import org.itsallcode.openfasttrace.importer.xmlparser.event.Attribute;
import org.itsallcode.openfasttrace.importer.xmlparser.event.StartElementEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jparams.verifier.tostring.ToStringVerifier;

@ExtendWith(MockitoExtension.class)
class TreeElementTest {
    @Mock
    StartElementEvent startElementMock;
    @Mock
    Consumer<TreeElement> listenerMock1;
    @Mock
    Consumer<TreeElement> listenerMock2;

    @Test
    void testToString() {
        ToStringVerifier.forClass(TreeElement.class).verify();
    }

    @Test
    void getElement() {
        assertThat(testee().getElement(), sameInstance(startElementMock));
    }

    @Test
    void getCharacterDataEmpty() {
        assertThat(testee().getCharacterData(), equalTo(""));
    }

    @Test
    void getCharacterData() {
        final TreeElement element = testee();
        element.addCharacterData("chars");
        assertThat(element.getCharacterData(), equalTo("chars"));
    }

    @Test
    void getCharacterDataMultipleAdded() {
        final TreeElement element = testee();
        element.addCharacterData("chars");
        element.addCharacterData(", more chars");
        assertThat(element.getCharacterData(), equalTo("chars, more chars"));
    }

    @Test
    void isRootTrueForNullParent() {
        assertThat(testee().isRootElement(), is(true));
    }

    @Test
    void isRootFalsForNonNullParent() {
        final TreeElement element = new TreeElement(startElementMock, mock(TreeElement.class));
        assertThat(element.isRootElement(), is(false));
    }

    @Test
    void getAttributeValueByName() {
        final Attribute attr = mock(Attribute.class);
        when(startElementMock.getAttributeValueByName("attr")).thenReturn(attr);
        assertThat(testee().getAttributeValueByName("attr"), sameInstance(attr));
    }

    @Test
    void getLocation() {
        final Location location = mock(Location.class);
        when(startElementMock.getLocation()).thenReturn(location);
        assertThat(testee().getLocation(), sameInstance(location));
    }

    @Test
    void invokeEndElementListenersSuccedsForEmptyList() {
        assertDoesNotThrow(testee()::invokeEndElementListeners);
    }

    @Test
    void invokeEndElementListenersCallsSingleEntry() {
        final TreeElement element = testee();
        element.addEndElementListener(listenerMock1);
        element.invokeEndElementListeners();
        verify(listenerMock1).accept(same(element));
    }

    @Test
    void invokeEndElementListenersCallsMultipleEntries() {
        final TreeElement element = testee();
        element.addEndElementListener(listenerMock1);
        element.addEndElementListener(listenerMock2);
        element.invokeEndElementListeners();
        final InOrder inOrder = inOrder(listenerMock1, listenerMock2);
        inOrder.verify(listenerMock1).accept(same(element));
        inOrder.verify(listenerMock2).accept(same(element));
        inOrder.verifyNoMoreInteractions();
    }

    private TreeElement testee() {
        return new TreeElement(startElementMock, null);
    }
}
