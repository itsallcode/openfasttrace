package openfasttrack.importer.legacytag;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2018 hamstercommunity
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

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.ImporterFactory;

public class LegacyTagImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger
            .getLogger(LegacyTagImporterFactory.class.getName());

    private final List<PathConfig> pathConfigs;

    public LegacyTagImporterFactory(final List<PathConfig> pathConfigs)
    {
        this.pathConfigs = pathConfigs;
    }

    @Override
    public boolean supportsFile(final Path file)
    {
        return findConfig(file).isPresent();
    }

    private Optional<PathConfig> findConfig(final Path file)
    {
        return this.pathConfigs.stream()
                .filter(config -> config.matches(file))
                .findFirst();
    }

    @Override
    public Importer createImporter(final Path file, final Charset charset,
            final ImportEventListener listener)
    {
        final Optional<PathConfig> config = findConfig(file);
        if (!config.isPresent())
        {
            throw new ImporterException("File '" + file + "' not supported for import");
        }
        LOG.finest(() -> "Creating importer for file " + file);
        final BufferedReader reader = createReader(file, charset);
        return new LegacyTagImporter(config.get(), file, reader, listener);
    }
}
