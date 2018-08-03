package org.itsallcode.openfasttrace.importer.tag;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.itsallcode.openfasttrace.importer.ImportEventListener;
import org.itsallcode.openfasttrace.importer.Importer;
import org.itsallcode.openfasttrace.importer.LineReader;
import org.itsallcode.openfasttrace.importer.LineReader.LineConsumer;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.config.PathConfig;

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
