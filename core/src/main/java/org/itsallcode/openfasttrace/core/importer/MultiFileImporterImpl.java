package org.itsallcode.openfasttrace.core.importer;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;

/**
 * This class allows you to import and collect {@link SpecificationItem}s from
 * multiple files.
 *
 * @see ImporterServiceImpl#createImporter()
 */
public class MultiFileImporterImpl implements MultiFileImporter
{
    private static final Logger LOG = Logger.getLogger(MultiFileImporterImpl.class.getName());
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String ALL_RECURSIVE_GLOB = "**/*";

    private final SpecificationListBuilder specItemBuilder;
    private final ImporterFactoryLoader factoryLoader;

    MultiFileImporterImpl(final SpecificationListBuilder specItemBuilder,
            final ImporterFactoryLoader factoryLoader)
    {
        this.specItemBuilder = specItemBuilder;
        this.factoryLoader = factoryLoader;
    }

    @Override
    public MultiFileImporterImpl importFile(final InputFile file)
    {
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        createImporterIfPossible(file, this.specItemBuilder).ifPresent(Importer::runImport);
        final int itemCountImported = this.specItemBuilder.getItemCount() - itemCountBefore;
        LOG.fine(() -> "Imported " + itemCountImported + " items from '" + file + "'.");
        return this;
    }

    @Override
    public MultiFileImporter importAny(final List<Path> paths)
    {
        for (final Path path : paths)
        {
            final File file = path.toFile();
            if (file.exists())
            {
                if (file.isDirectory())
                {
                    importRecursiveDir(path, ALL_RECURSIVE_GLOB);
                }
                else
                {
                    importFile(RealFileInput.forPath(path));
                }
            }
            else
            {
                LOG.warning(() -> "No such input file or directory \"" + path.toString()
                        + "\". Skipping.");
            }
        }
        return this;
    }

    // [impl->dsn~input-directory-recursive-traversal~1]
    @Override
    public MultiFileImporter importRecursiveDir(final Path dir, final String glob)
    {
        final PathMatcher matcher = dir.getFileSystem().getPathMatcher("glob:" + glob);
        final AtomicInteger fileCount = new AtomicInteger(0);
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        try (Stream<Path> fileStream = Files.walk(dir))
        {
            fileStream.filter(path -> !path.toFile().isDirectory()) //
                    .filter(matcher::matches) //
                    .map(path -> RealFileInput.forPath(path, DEFAULT_CHARSET))
                    .filter(this.factoryLoader::supportsFile)
                    .map(file -> createImporterIfPossible(file, this.specItemBuilder)).forEach(importer -> {
                        importer.ifPresent(Importer::runImport);
                        fileCount.incrementAndGet();
                    });
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error walking directory " + dir, e);
        }
        final int itemCountImported = this.specItemBuilder.getItemCount() - itemCountBefore;
        LOG.fine(() -> "Imported " + fileCount + " files containing " + itemCountImported
                + " items from '" + dir + "'.");
        return this;
    }

    @Override
    public List<SpecificationItem> getImportedItems()
    {
        return this.specItemBuilder.build();
    }

    private Optional<Importer> createImporterIfPossible(final InputFile file, final SpecificationListBuilder builder)
    {
        final Optional<ImporterFactory> importerFactory = this.factoryLoader.getImporterFactory(file);
        final Optional<Importer> importer = importerFactory.isPresent()
                ? Optional.of(importerFactory.get().createImporter(file, builder))
                : Optional.empty();

        LOG.fine(() -> (importer.isPresent() ? "Created importer of type '" + importer.getClass().getSimpleName()
                : "No import")
                + "' for file '" + file + "'");
        return importer;
    }

}
