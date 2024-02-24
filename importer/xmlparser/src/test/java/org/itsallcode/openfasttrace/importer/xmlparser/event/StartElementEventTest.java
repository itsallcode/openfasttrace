package org.itsallcode.openfasttrace.importer.xmlparser.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import javax.xml.namespace.QName;

import org.itsallcode.openfasttrace.api.core.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

import com.jparams.verifier.tostring.ToStringVerifier;

@ExtendWith(MockitoExtension.class)
class StartElementEventTest
{
    private static final Location LOCATION = Location.create("path", 42);
    private static final String URI = "uri";
    private static final String LOCAL_NAME = "localName";
    private static final String QNAME = "qname";

    @Mock
    Attributes attributesMock;

    @Test
    void testToString()
    {
        ToStringVerifier.forClass(StartElementEvent.class).verify();
    }

    @Test
    void getName()
    {
        final QName qName = testee().getName();
        assertAll(() -> assertThat(qName.getLocalPart(), equalTo("localName")),
                () -> assertThat(qName.getNamespaceURI(), equalTo("uri")),
                () -> assertThat(qName.getPrefix(), equalTo("")));
    }

    @Test
    void getLocation()
    {
        assertThat(testee().getLocation(), sameInstance(LOCATION));
    }

    @Test
    void getAttributeValueByNameExists()
    {
        final Attribute attr = testee().getAttributeValueByName("qname0");
        assertAll(() -> assertThat(attr.getValue(), equalTo("value0")),
                () -> assertThat(attr.getQname(), equalTo("qname0")));
    }

    @Test
    void getAttributeValueByNameDoesNotExist()
    {
        assertThat(testee().getAttributeValueByName("missing"), nullValue());
    }

    private StartElementEvent testee()
    {
        simulateAttributes();
        return StartElementEvent.create(URI, LOCAL_NAME, QNAME, attributesMock, LOCATION);
    }

    private void simulateAttributes()
    {
        when(attributesMock.getLength()).thenReturn(2);
        when(attributesMock.getQName(0)).thenReturn("qname0");
        when(attributesMock.getValue(0)).thenReturn("value0");
        when(attributesMock.getQName(1)).thenReturn("qname1");
        when(attributesMock.getValue(1)).thenReturn("value1");
    }
}
