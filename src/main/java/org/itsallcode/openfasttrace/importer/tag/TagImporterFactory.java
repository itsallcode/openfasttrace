package org.itsallcode.openfasttrace.importer.tag;

import static java.util.stream.Collectors.toList;

import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
import org.itsallcode.openfasttrace.importer.*;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.itsallcode.openfasttrace.importer.tag.config.PathConfig;
import org.itsallcode.openfasttrace.importer.tag.config.TagImporterConfig;

/**
 * {@link ImporterFactory} for tags in source code files.
 */
// [impl->dsn~import.full-coverage-tag~1]
public class TagImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger.getLogger(TagImporterFactory.class.getName());

    private static final String DEFAULT_FILE_REGEX = "(?i).*\\.java";
    private static final Pattern DEFAULT_FILE_PATTERN = Pattern.compile(DEFAULT_FILE_REGEX);

    @Override
    public boolean supportsFile(final InputFile path)
    {
        return supportsDefaultFile(path) || supportsConfiguredFile(path);
    }

    private boolean supportsConfiguredFile(final InputFile path)
    {
        return findConfig(path).isPresent();
    }

    public boolean supportsDefaultFile(final InputFile file)
    {
        return DEFAULT_FILE_PATTERN.matcher(file.getPath()).matches();
    }

    private Optional<PathConfig> findConfig(final InputFile file)
    {
        return getPathConfigs()//
                .peek(config -> LOG
                        .finest(() -> "Checking config " + config + " with file " + file))
                .filter(config -> config.matches(file)) //
                .peek(config -> LOG.finest(() -> "Config " + config + " matches file " + file)) //
                .findFirst();
    }

    @Override
    public Importer createImporter(final InputFile path, final ImportEventListener listener)
    {
        if (!supportsFile(path))
        {
            throw new ImporterException("File '" + path
                    + "' cannot be imported because it does not match any supported file patterns: "
                    + DEFAULT_FILE_REGEX + " and " + getPathConfigs().collect(toList()));
        }
        final Optional<PathConfig> config = findConfig(path);
        return TagImporter.create(config, path, listener);
    }

    private Stream<PathConfig> getPathConfigs()
    {
        final TagImporterConfig config = getContext().getTagImporterConfig();
        return config == null ? Stream.empty() : config.getPathConfigs().stream();
    }
}
