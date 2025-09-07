package org.itsallcode.openfasttrace.importer.specobject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.xmlparser.XmlParserFactory;

/**
 * An {@link ImporterFactory} for ReqM2/SpecObject XML files.
 */
public class SpecobjectImporterFactory extends RegexMatchingImporterFactory
{
    private static final Logger LOG = Logger.getLogger(SpecobjectImporterFactory.class.getName());
    private static final int PEEK_CHARS = 4096;

    private final XmlParserFactory xmlParserFactory;

    /**
     * Create a new instance.
     */
    public SpecobjectImporterFactory()
    {
        super("(?i).*\\.(xml|oreqm)");
        this.xmlParserFactory = new XmlParserFactory();
    }

    // [impl -> dsn~import.reqm2-file-detection~1]
    @Override
    public boolean supportsFile(final InputFile file)
    {
        final String path = file.getPath();
        final String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".oreqm"))
        {
            return true;
        }
        else if (lower.endsWith(".xml"))
        {
            try (BufferedReader reader = file.createReader())
            {
                final char[] buf = new char[PEEK_CHARS];
                final int read = reader.read(buf);
                if (read <= 0)
                {
                    return false;
                }
                else {
                    final String header = new String(buf, 0, read);
                    return header.contains("<specdocument");
                }
            }
            catch (final IOException exception)
            {
                LOG.fine(() -> "Unable to peek XML file '" + path + "' trying to determine if it contains ReqM2 format: " + exception.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new SpecobjectImporter(file, this.xmlParserFactory, listener);
    }
}
