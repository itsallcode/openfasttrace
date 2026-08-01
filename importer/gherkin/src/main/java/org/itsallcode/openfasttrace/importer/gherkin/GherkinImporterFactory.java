package org.itsallcode.openfasttrace.importer.gherkin;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/** Factory for importing OpenFastTrace specifications from Gherkin files. */
// [impl->dsn~gherkin.importer-selection~1]
public class GherkinImporterFactory extends AbstractRegexMatchingImporterFactory
{
    /** Create a factory that accepts Gherkin {@code .feature} files. */
    public GherkinImporterFactory()
    {
        super("(?i).*\\.feature");
    }

    @Override
    public int getPriority()
    {
        return 9000;
    }

    @Override
    public Importer createImporter(final InputFile file, final ImportEventListener listener)
    {
        return new GherkinImporter(file, listener);
    }
}
