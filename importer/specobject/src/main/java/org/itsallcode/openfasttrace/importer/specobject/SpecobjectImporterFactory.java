package org.itsallcode.openfasttrace.importer.specobject;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.xmlparser.XmlParserFactory;

/**
 * An {@link ImporterFactory} for XML specobject files.
 */
public class SpecobjectImporterFactory extends RegexMatchingImporterFactory
{
    private final XmlParserFactory xmlParserFactory;

    /**
     * Create a new instance.
     */
    public SpecobjectImporterFactory()
    {
        super("(?i).*\\.(xml|oreqm)");
        this.xmlParserFactory = new XmlParserFactory();
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new SpecobjectImporter(file, this.xmlParserFactory, listener);
    }
}
