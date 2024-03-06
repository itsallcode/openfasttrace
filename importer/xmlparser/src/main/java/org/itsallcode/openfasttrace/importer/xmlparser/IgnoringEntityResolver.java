package org.itsallcode.openfasttrace.importer.xmlparser;

import java.io.StringReader;
import java.util.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An {@link EntityResolver} that ignores all entities.
 */
class IgnoringEntityResolver implements EntityResolver
{
    private static final Logger LOG = Logger.getLogger(IgnoringEntityResolver.class.getName());

    /**
     * Create a new {@link IgnoringEntityResolver}.
     */
    IgnoringEntityResolver()
    {
        // empty by default
    }

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId)
    {
        LOG.warning(() -> "Ignoring entity with public id '" + publicId + "' and system id '"
                + systemId + "'.");
        return new InputSource(new StringReader(""));
    }
}
