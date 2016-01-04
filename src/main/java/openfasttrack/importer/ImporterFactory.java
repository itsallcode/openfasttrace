package openfasttrack.importer;

import java.io.Reader;

import openfasttrack.importer.markdown.MarkdownImporter;

/**
 * Factory for importers
 */
public class ImporterFactory
{
    /**
     * Create an importer that is able to read the given file
     *
     * @param reader
     *            the reader from which specification items are imported
     * @param listener
     *            the listener to be informed about detected specification item
     *            fragments
     * @return an importer instance
     */
    public static Importer createImporter(final Reader reader, final ImportEventListener listener)
    {
        return new MarkdownImporter(reader, listener);
    }
}