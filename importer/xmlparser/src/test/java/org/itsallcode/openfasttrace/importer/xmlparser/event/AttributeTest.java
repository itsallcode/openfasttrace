package org.itsallcode.openfasttrace.importer.xmlparser.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.Attributes;

import com.jparams.verifier.tostring.ToStringVerifier;

@ExtendWith(MockitoExtension.class)
class AttributeTest {

    @Mock
    Attributes attributesMock;

    @Test
    void testToString() {
        ToStringVerifier.forClass(Attribute.class).verify();
    }

    @Test
    void buildEmptyMap() {
        final Map<String, Attribute> map = Attribute.buildMap(attributesMock);
        assertThat(map.size(), is(0));
    }

    @Test
    void buildMapWithSingleElement() {
        when(attributesMock.getLength()).thenReturn(1);
        when(attributesMock.getQName(0)).thenReturn("qname0");
        when(attributesMock.getValue(0)).thenReturn("value0");
        final Map<String, Attribute> map = Attribute.buildMap(attributesMock);
        assertAll(() -> assertThat(map.size(), is(1)),
                () -> assertThat(map.get("qname0").getQname(), equalTo("qname0")),
                () -> assertThat(map.get("qname0").getValue(), equalTo("value0")));
    }

    @Test
    void buildMapWithMultiple() {
        when(attributesMock.getLength()).thenReturn(2);
        when(attributesMock.getQName(0)).thenReturn("qname0");
        when(attributesMock.getValue(0)).thenReturn("value0");
        when(attributesMock.getQName(1)).thenReturn("qname1");
        when(attributesMock.getValue(1)).thenReturn("value1");
        final Map<String, Attribute> map = Attribute.buildMap(attributesMock);
        assertAll(() -> assertThat(map.size(), is(2)),
                () -> assertThat(map.get("qname0"), notNullValue()),
                () -> assertThat(map.get("qname1"), notNullValue()));
    }
}
