package org.itsallcode.openfasttrace.api.core;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSourcePosition
{
    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(SourcePosition.class).verify();
    }
}
