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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import openfasttrack.importer.*;

public class LegacyTagImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger
            .getLogger(LegacyTagImporterFactory.class.getName());

    private static LegacyTagImporterConfig defaultConfig = new LegacyTagImporterConfig();

    private final List<PathConfig> pathConfigs;

    public LegacyTagImporterFactory()
    {
        this(defaultConfig.getPathConfigs());
    }

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
            final List<String> patterns = this.pathConfigs.stream()
                    .map(pathConfig -> pathConfig.getPattern())
                    .collect(toList());
            throw new ImporterException(
                    "File '" + file + "' not supported for import, supported patterns: "
                            + patterns);
        }
        return () -> runImporter(file, charset, config.get(), listener);
    }

    private void runImporter(final Path file, final Charset charset, final PathConfig config,
            final ImportEventListener listener)
    {
        LOG.finest(() -> "Creating importer for file " + file);
        try (final LineReader reader = LineReader.create(file, charset))
        {
            final LegacyTagImporter importer = new LegacyTagImporter(config, file, reader,
                    listener);
            importer.runImport();
        }
        catch (final IOException e)
        {
            throw new ImporterException(
                    "Error importing file '" + file + "': " + e.getMessage(), e);
        }
    }

    public static void setPathConfig(final LegacyTagImporterConfig config)
    {
        defaultConfig = config;
    }
}
