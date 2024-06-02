package org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

import org.junit.jupiter.api.Test;

import com.jparams.verifier.tostring.ToStringVerifier;

import nl.jqno.equalsverifier.EqualsVerifier;

class LineContextTest
{
    @Test
    void testEqualsAndHashContract()
    {
        EqualsVerifier.forClass(LineContext.class).verify();
    }

    @Test
    void testToString()
    {
        ToStringVerifier.forClass(LineContext.class).verify();
    }
}
