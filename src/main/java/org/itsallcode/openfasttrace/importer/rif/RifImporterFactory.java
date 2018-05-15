package org.itsallcode.openfasttrace.importer.rif;

import java.io.Reader;

import javax.xml.parsers.SAXParserFactory;

import org.itsallcode.openfasttrace.core.xml.SaxParserConfigurator;
import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.RegexMatchingImporterFactory;

public class RifImporterFactory extends RegexMatchingImporterFactory
{
    private final SAXParserFactory saxParserFactory;

    public RifImporterFactory()
    {
        super("(?i).*\\.(rif)");
        this.saxParserFactory = SaxParserConfigurator.createSaxParserFactory();
    }

    @Override
    public Importer createImporter(final String fileName, final Reader reader,
            final ImportEventListener listener)
    {
        return new RifImporter(fileName, reader, this.saxParserFactory, listener);
    }

}
