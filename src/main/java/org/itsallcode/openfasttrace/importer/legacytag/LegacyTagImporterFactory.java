package org.itsallcode.openfasttrace.importer.legacytag;

/*-
 * #%L
 \* OpenFastTrace
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
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.importer.*;

public class LegacyTagImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger.getLogger(LegacyTagImporterFactory.class.getName());

    private static LegacyTagImporterConfig defaultConfig = LegacyTagImporterConfig.empty();;

    private final Supplier<LegacyTagImporterConfig> config;

    public LegacyTagImporterFactory()
    {
        this(() -> defaultConfig);
    }

    public LegacyTagImporterFactory(final Supplier<LegacyTagImporterConfig> config)
    {
        this.config = config;
    }

    @Override
    public boolean supportsFile(final Path path)
    {
        return findConfig(path).isPresent();
    }

    private Optional<PathConfig> findConfig(final Path path)
    {
        final Path relativePath = makeRelative(path);
        return this.config.get().getPathConfigs().stream() //
                .filter(config -> config.matches(relativePath)) //
                .findFirst();
    }

    private Path makeRelative(final Path path)
    {
        return this.config.get().getBasePath() //
                .map(basePath -> basePath.relativize(path)) //
                .orElse(path);
    }

    @Override
    public Importer createImporter(final Path path, final Charset charset,
            final ImportEventListener listener)
    {
        final Optional<PathConfig> config = findConfig(path);
        if (!config.isPresent())
        {
            final List<String> patterns = this.config.get().getPathConfigs().stream() //
                    .map(PathConfig::getPattern) //
                    .collect(toList());
            throw new ImporterException("File '" + path
                    + "' not supported for import, supported patterns: " + patterns);
        }
        return () -> runImporter(path, charset, config.get(), listener);
    }

    private void runImporter(final Path path, final Charset charset, final PathConfig config,
            final ImportEventListener listener)
    {
        LOG.finest(() -> "Creating importer for file " + path);
        try (final LineReader reader = LineReader.create(path, charset))
        {
            final LegacyTagImporter importer = new LegacyTagImporter(config, path, reader,
                    listener);
            importer.runImport();
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error importing file '" + path + "': " + e.getMessage(),
                    e);
        }
    }

    public static void setPathConfig(final LegacyTagImporterConfig config)
    {
        defaultConfig = config;
    }
}
