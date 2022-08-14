package org.itsallcode.openfasttrace.importer.specobject;

import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.specobject.xml.SaxParserConfigurator;

/**
 * An {@link ImporterFactory} for XML specobject files.
 */
public class SpecobjectImporterFactory extends RegexMatchingImporterFactory
{
    private final SAXParserFactory saxParserFactory;

    /**
     * Create a new instance.
     */
    public SpecobjectImporterFactory()
    {
        super("(?i).*\\.(xml|oreqm)");
        this.saxParserFactory = SaxParserConfigurator.createSaxParserFactory();
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new SpecobjectImporter(file, this.saxParserFactory, listener);
    }
}
