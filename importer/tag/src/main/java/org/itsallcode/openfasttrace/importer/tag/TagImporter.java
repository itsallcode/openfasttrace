package org.itsallcode.openfasttrace.importer.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.LineReader.LineConsumer;

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

    public static TagImporter create(final Optional<PathConfig> config, final InputFile file,
            final ImportEventListener listener)
    {
        final LineConsumer lineConsumer = createLineConsumer(config, file, listener);
        return new TagImporter(lineConsumer, file);
    }

    private static LineConsumer createLineConsumer(final Optional<PathConfig> config,
            final InputFile file, final ImportEventListener listener)
    {
        final List<LineConsumer> importers = new ArrayList<>();
        importers.add(new LongTagImportingLineConsumer(file, listener));
        if (config.isPresent())
        {
            importers.add(new ShortTagImportingLineConsumer(config.get(), file, listener));
        }
        return new DelegatingLineConsumer(importers);
    }

    @Override
    public void runImport()
    {
        final LineReader reader = LineReader.create(this.file);
        reader.readLines(this.lineImporter);
    }
}
