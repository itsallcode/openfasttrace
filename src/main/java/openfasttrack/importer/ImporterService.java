package openfasttrack.importer;

import java.io.IOException;

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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
 */
public class ImporterService
{
    private static Logger LOG = Logger.getLogger(ImporterService.class.getName());
    private final ImporterFactoryLoader factoryLoader;

    public ImporterService()
    {
        this(new ImporterFactoryLoader());
    }

    ImporterService(final ImporterFactoryLoader factoryLoader)
    {
        this.factoryLoader = factoryLoader;
    }

    public Map<SpecificationItemId, SpecificationItem> importRecursiveDir(final Path dir,
            final String glob)
    {
        final SpecificationMapListBuilder builder = new SpecificationMapListBuilder();
        importRecursiveDir(dir, glob, builder);
        return builder.build();
    }

    private void importRecursiveDir(final Path dir, final String glob,
            final SpecificationMapListBuilder builder)
    {
        LOG.info(() -> "Importing files from '" + dir + "'...");
        final FileSystem fs = dir.getFileSystem();
        final PathMatcher matcher = fs.getPathMatcher("glob:" + glob);
        final AtomicInteger fileCount = new AtomicInteger(0);
        try (Stream<Path> fileStream = Files.walk(dir))
        {
            fileStream.filter(path -> !Files.isDirectory(path)) //
                    .filter(matcher::matches) //
                    .filter(this.factoryLoader::supportsFile)
                    .map(file -> createImporter(file, StandardCharsets.UTF_8, builder))
                    .forEach((importer) -> {
                        importer.runImport();
                        fileCount.incrementAndGet();
                    });
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error walking directory " + dir, e);
        }
        LOG.info(() -> "Imported " + fileCount + " files from '" + dir + "'.");
    }

    /**
     * Import the given file using the matching {@link Importer} using character
     * set {@link StandardCharsets#UTF_8 UTF-8} for reading.
     *
     * @param file
     *            the file to import.
     * @return a {@link List} of {@link SpecificationItem} imported from the
     *         file.
     */
    public Map<SpecificationItemId, SpecificationItem> importFile(final Path file)
    {
        return importFile(file, StandardCharsets.UTF_8);
    }

    /**
     * Import the given file using the matching {@link Importer}.
     *
     * @param file
     *            the file to import.
     * @param charset
     *            the {@link Charset} used for reading the file.
     * @return a {@link Map} of {@link SpecificationItem} imported from the
     *         file.
     */
    public Map<SpecificationItemId, SpecificationItem> importFile(final Path file,
            final Charset charset)
    {
        final SpecificationMapListBuilder builder = new SpecificationMapListBuilder();
        final Importer importer = createImporter(file, charset, builder);
        importer.runImport();
        return builder.build();
    }

    private Importer createImporter(final Path file, final Charset charset,
            final SpecificationMapListBuilder builder)
    {
        final ImporterFactory importerFactory = this.factoryLoader.getImporterFactory(file);
        final Importer importer = importerFactory.createImporter(file, charset, builder);
        LOG.fine(() -> "Created importer of type " + importer.getClass().getSimpleName()
                + " for file " + file);
        return importer;
    }
}
