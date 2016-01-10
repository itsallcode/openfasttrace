package openfasttrack.importer.specobject;

import java.io.Reader;

import javax.xml.stream.XMLInputFactory;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterFactory;

/**
 * {@link ImporterFactory} for xml specobject files
 */
public class SpecobjectImporterFactory extends ImporterFactory
{
    private final XMLInputFactory xmlInputFactory;

    public SpecobjectImporterFactory()
    {
        super("(?i).*\\.xml");
        this.xmlInputFactory = XMLInputFactory.newFactory();
    }

    @Override
    public Importer createImporter(final Reader reader, final ImportEventListener listener)
    {
        return new SpecobjectImporter(reader, this.xmlInputFactory, listener);
    }
}
