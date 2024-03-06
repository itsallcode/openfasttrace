package org.itsallcode.openfasttrace.importer.xmlparser;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;

class IgnoringEntityResolverTest
{
    @Test
    void testResolveEntityReturnsEmptyInput()
    {
        final InputSource source = new IgnoringEntityResolver().resolveEntity(null, null);
        assertTrue(source.isEmpty());
    }
}
