package org.itsallcode.openfasttrace.importer.xmlparser.event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertAll;

import javax.xml.namespace.QName;

import org.itsallcode.openfasttrace.api.core.Location;
import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

class EndElementEventTest
{
    private static final Location LOCATION = Location.create("path", 42);

    @Test
    void testToString()
    {
        ToStringVerifier.forClass(EndElementEvent.class).verify();
    }

    @Test
    void testGetName()
    {
        final QName qName = testee().getName();
        assertAll(() -> assertThat(qName.getLocalPart(), equalTo("localName")),
                () -> assertThat(qName.getNamespaceURI(), equalTo("uri")),
                () -> assertThat(qName.getPrefix(), equalTo("")));
    }

    @Test
    void testGetLocation()
    {
        assertThat(testee().getLocation(), sameInstance(LOCATION));
    }

    private EndElementEvent testee()
    {
        return EndElementEvent.create("uri", "localName", "qname", LOCATION);
    }
}
