package org.itsallcode.openfasttrace.api.core;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestLocatedSpecificationItemId
{
    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(LocatedSpecificationItemId.class).verify();
    }
}
