package org.itsallcode.openfasttrace.importer;

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
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.importer.input.InputFile;

/**
 * This class allows you to import and collect {@link SpecificationItem}s from
 * multiple files.
 * 
 * @see ImporterService#createImporter()
 */
public class MultiFileImporter
{
    private static final Logger LOG = Logger.getLogger(MultiFileImporter.class.getName());
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String ALL_RECURSIVE_GLOB = "**/*";

    private final SpecificationListBuilder specItemBuilder;
    private final ImporterFactoryLoader factoryLoader;

    MultiFileImporter(final SpecificationListBuilder specItemBuilder,
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
        LOG.fine(() -> "Importing file '" + file + "'...");
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        final InputFile inputFile = InputFile.createForPath(file, DEFAULT_CHARSET);
        createImporter(inputFile, this.specItemBuilder).runImport();
        final int itemCountImported = this.specItemBuilder.getItemCount() - itemCountBefore;
        LOG.fine(() -> "Imported " + itemCountImported + " items from '" + file + "'.");
        return this;
    }

    /**
     * Import from the path, independently of whether it is represents a
     * directory or a file.
     * 
     * @param paths
     *            lists of paths to files or directories
     * @return <code>this</code> for fluent programming style.
     */
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
                else if (file.isFile())
                {
                    importFile(path);
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

    /**
     * Import all files from the given directory that match the given
     * {@link FileSystem#getPathMatcher(String) glob expression} and character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param dir
     *            the dir to search for files to import.
     * @param glob
     *            the {@link FileSystem#getPathMatcher(String) glob expression}
     * @return <code>this</code> for fluent programming style.
     */
    // [impl->dsn~input-directory-recursive-traversal~1]
    public MultiFileImporter importRecursiveDir(final Path dir, final String glob)
    {
        LOG.fine(() -> "Importing files from '" + dir + "' matching glob '" + glob + "'...");
        final PathMatcher matcher = dir.getFileSystem().getPathMatcher("glob:" + glob);
        final AtomicInteger fileCount = new AtomicInteger(0);
        final int itemCountBefore = this.specItemBuilder.getItemCount();
        try (Stream<Path> fileStream = Files.walk(dir))
        {
            fileStream.filter(path -> !path.toFile().isDirectory()) //
                    .filter(matcher::matches) //
                    .map(path -> InputFile.createForPath(path, DEFAULT_CHARSET))
                    .filter(this.factoryLoader::supportsFile)
                    .map(file -> createImporter(file, this.specItemBuilder)).forEach(importer -> {
                        importer.runImport();
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

    /**
     * Get all imported {@link SpecificationItem}s.
     * 
     * @return all imported {@link SpecificationItem}s.
     */
    public List<SpecificationItem> getImportedItems()
    {
        return this.specItemBuilder.build();
    }

    private Importer createImporter(final InputFile file, final SpecificationListBuilder builder)
    {
        final ImporterFactory importerFactory = this.factoryLoader.getImporterFactory(file);
        final Importer importer = importerFactory.createImporter(file, builder);
        LOG.fine(() -> "Created importer of type '" + importer.getClass().getSimpleName()
                + "' for file '" + file + "'");
        return importer;
    }
}
