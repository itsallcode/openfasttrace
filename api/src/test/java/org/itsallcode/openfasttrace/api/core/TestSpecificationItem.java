package org.itsallcode.openfasttrace.api.core;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

class TestSpecificationItem
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass(SpecificationItem.class).verify();
    }
}
