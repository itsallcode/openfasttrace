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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;

/**
 * This service provides convenient methods for importing
 * {@link SpecificationItem}s that automatically use the correct
 * {@link Importer} based on the filename.
 */
public class ImporterService
{
    private final ImporterFactoryLoader factoryLoader;

    public ImporterService()
    {
        this(new ImporterFactoryLoader());
    }

    ImporterService(final ImporterFactoryLoader factoryLoader)
    {
        this.factoryLoader = factoryLoader;
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
        return importerFactory.createImporter(file, charset, builder);
    }
}
