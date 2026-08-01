package org.itsallcode.openfasttrace.importer.gherkin;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader;

/** Imports annotated Gherkin scenarios while streaming the input once. */
// [impl->dsn~gherkin.streaming-import~1]
final class GherkinImporter implements Importer
{
    private final InputFile file;
    private final GherkinLineConsumer lineConsumer;

    GherkinImporter(final InputFile file, final ImportEventListener listener)
    {
        this.file = file;
        this.lineConsumer = new GherkinLineConsumer(file, listener);
    }

    @Override
    public void runImport()
    {
        LineReader.create(this.file).readLines(this.lineConsumer);
    }
}
