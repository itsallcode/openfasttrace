package org.itsallcode.openfasttrace.api.core;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSourceRange
{
    @Test
    void testEqualsContract()
    {
        EqualsVerifier.forClass(SourceRange.class).verify();
    }
}
