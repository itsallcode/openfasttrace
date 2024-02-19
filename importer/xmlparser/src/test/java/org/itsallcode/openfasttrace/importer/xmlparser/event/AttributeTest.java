package org.itsallcode.openfasttrace.importer.xmlparser.event;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

class AttributeTest {

    @Test
    void testToString() {
        ToStringVerifier.forClass(Attribute.class).verify();
    }
}
