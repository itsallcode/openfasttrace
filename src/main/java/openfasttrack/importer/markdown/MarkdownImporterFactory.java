package openfasttrack.importer.markdown;

import java.io.Reader;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterFactory;

/**
 * {@link ImporterFactory} for Markdown files
 */
public class MarkdownImporterFactory extends ImporterFactory
{
    public MarkdownImporterFactory()
    {
        super(".md");
    }

    @Override
    public Importer createImporter(final Reader reader, final ImportEventListener listener)
    {
        return new MarkdownImporter(reader, listener);
    }
}