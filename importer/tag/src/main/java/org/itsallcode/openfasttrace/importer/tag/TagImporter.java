package org.itsallcode.openfasttrace.importer.tag;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.common.CoverageTagParser;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader;
import org.itsallcode.openfasttrace.importer.tag.common.LineReader.LineConsumer;

/**
 * {@link Importer} for tags in source code files.
 */
// [impl->dsn~import.full-coverage-tag~1]
class TagImporter implements Importer
{
    private final LineConsumer lineImporter;
    private final InputFile file;

    TagImporter(final LineConsumer lineImporter, final InputFile file)
    {
        this.lineImporter = lineImporter;
        this.file = file;
    }

    static TagImporter create(final PathConfig config, final InputFile file,
            final ImportEventListener listener)
    {
        return new TagImporter(CoverageTagParser.create(config, file, listener), file);
    }

    @Override
    public void runImport()
    {
        final LineReader reader = LineReader.create(this.file);
        reader.readLines(this.lineImporter);
    }
}
