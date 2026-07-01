package org.itsallcode.openfasttrace.importer.restructuredtext;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * {@link ImporterFactory} for reStructuredText files
 */
public class RestructuredTextImporterFactory extends AbstractRegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public RestructuredTextImporterFactory()
    {
        super("(?i).*\\.rst");
    }

    @Override
    public int getPriority() {
        return 2000;
    }

    @Override
    public Importer createImporter(final InputFile fileName, final ImportEventListener listener)
    {
        return new RestructuredTextImporter(fileName, listener);
    }
}
