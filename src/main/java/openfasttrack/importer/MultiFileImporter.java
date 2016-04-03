package openfasttrack.importer;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * This class allows you to import and collect {@link SpecificationItem}s from
 * multiple files.
 * 
 * @see ImporterService#createImporter()
 */
public class MultiFileImporter
{
    private static Logger LOG = Logger.getLogger(MultiFileImporter.class.getName());

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final SpecificationMapListBuilder specItemBuilder;
    private final ImporterFactoryLoader factoryLoader;

    MultiFileImporter(final SpecificationMapListBuilder specItemBuilder,
            final ImporterFactoryLoader factoryLoader)
    {
        this.specItemBuilder = specItemBuilder;
        this.factoryLoader = factoryLoader;
    }

    /**
     * Import the given file using the matching {@link Importer} using character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param file
     *            the file to import.
     * @return <code>this</code> for fluent programming style.
     */
    public MultiFileImporter importFile(final Path file)
    {
        LOG.info(() -> "Importing file '" + file + "'...");
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        createImporter(file, DEFAULT_CHARSET, this.specItemBuilder).runImport();
        final int itemCountImported = this.specItemBuilder.getItemCount() - itemCountBefore;
        LOG.info(() -> "Imported " + itemCountImported + " items from '" + file + "'.");
        return this;
    }

    /**
     * Import all files from the given directory that match the given
     * {@link FileSystem#getPathMatcher(String) glob expression} using character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param dir
     *            the dir to search for files to import.
     * @param glob
     *            the {@link FileSystem#getPathMatcher(String) glob expression}
     * @return <code>this</code> for fluent programming style.
     */
    public MultiFileImporter importRecursiveDir(final Path dir, final String glob)
    {
        LOG.info(() -> "Importing files from '" + dir + "'...");
        final PathMatcher matcher = dir.getFileSystem().getPathMatcher("glob:" + glob);
        final AtomicInteger fileCount = new AtomicInteger(0);
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        try (Stream<Path> fileStream = Files.walk(dir))
        {
            fileStream.filter(path -> !Files.isDirectory(path)) //
                    .filter(matcher::matches) //
                    .filter(this.factoryLoader::supportsFile)
                    .map(file -> createImporter(file, DEFAULT_CHARSET, this.specItemBuilder))
                    .forEach((importer) -> {
                        importer.runImport();
                        fileCount.incrementAndGet();
                    });
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error walking directory " + dir, e);
        }
        final int itemCountImported = this.specItemBuilder.getItemCount() - itemCountBefore;
        LOG.info(() -> "Imported " + fileCount + " files containing " + itemCountImported
                + " items from '" + dir + "'.");
        return this;
    }

    /**
     * Get all imported {@link SpecificationItem}s.
     * 
     * @return all imported {@link SpecificationItem}s.
     */
    public Map<SpecificationItemId, SpecificationItem> getImportedItems()
    {
        return this.specItemBuilder.build();
    }

    private Importer createImporter(final Path file, final Charset charset,
            final SpecificationMapListBuilder builder)
    {
        final ImporterFactory importerFactory = this.factoryLoader.getImporterFactory(file);
        final Importer importer = importerFactory.createImporter(file, charset, builder);
        LOG.fine(() -> "Created importer of type '" + importer.getClass().getSimpleName()
                + "' for file '" + file + "'");
        return importer;
    }
}
