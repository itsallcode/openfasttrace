package org.itsallcode.openfasttrace.importer.xmlparser.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QNameFactoryTest
{
    @ParameterizedTest
    @CsvSource(nullValues = "NULL", value =
    { "NULL", "''" })
    void testCreateWithoutLocalName(final String localName)
    {
        final QName qName = QNameFactory.create("namespaceUri", localName, "qname");
        assertAll(() -> assertThat(qName.getLocalPart(), equalTo("qname")),
                () -> assertThat(qName.getNamespaceURI(), equalTo("namespaceUri")),
                () -> assertThat(qName.getPrefix(), equalTo("")));
    }

    @Test
    void testCreateWithLocalName()
    {
        final QName qName = QNameFactory.create("namespaceUri", "localName", "qname");
        assertAll(() -> assertThat(qName.getLocalPart(), equalTo("localName")),
                () -> assertThat(qName.getNamespaceURI(), equalTo("namespaceUri")),
                () -> assertThat(qName.getPrefix(), equalTo("")));
    }
}
