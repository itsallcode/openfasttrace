package openfasttrack.importer.tag;

import java.io.Reader;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterFactory;

/**
 * {@link ImporterFactory} for tags in source code files.
 */
public class TagImporterFactory extends ImporterFactory
{
    public TagImporterFactory()
    {
        super("(?i).*\\.java");
    }

    @Override
    public Importer createImporter(final Reader reader, final ImportEventListener listener)
    {
        return new TagImporter(reader, listener);
    }
}
