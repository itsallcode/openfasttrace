package openfasttrack.core.xml;

import java.io.StringReader;
import java.util.logging.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class IgnoringEntityResolver implements EntityResolver
{
    private static final Logger LOG = Logger.getLogger(IgnoringEntityResolver.class.getName());

    @Override
    public InputSource resolveEntity(final String publicId, final String systemId)
    {
        LOG.warning("Ignoring entity " + publicId + " / " + systemId);
        return new InputSource(new StringReader(""));
    }
}
