package org.itsallcode.openfasttrace.importer.markdown;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * {@link ImporterFactory} for Markdown files
 */
public class MarkdownImporterFactory extends RegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public MarkdownImporterFactory()
    {
        super("(?i).*\\.markdown", "(?i).*\\.md");
    }

    @Override
    public Importer createImporter(final InputFile fileName, final ImportEventListener listener)
    {
        return new MarkdownImporter(fileName, listener);
    }
}
