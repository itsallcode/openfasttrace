package org.itsallcode.openfasttrace.importer.tag;

/*-
 * #%L
 * OpenFastTrace Tag Importer
 * %%
 * Copyright (C) 2016 - 2020 itsallcode.org
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;

/**
 * {@link ImporterFactory} for tags in source code files.
 */
// [impl->dsn~import.full-coverage-tag~1]
public class TagImporterFactory extends ImporterFactory
{
    private static final String DEFAULT_FILE_REGEX = "(?i).*\\.java";
    private static final List<String> SUPPORTED_DEFAULT_EXTENSIONS = Arrays.asList( //
            "bat", // Windows batch files
            "c", "C", "cc", "cpp", "c++", "h", "H", "h++", "hh", "hpp", // C/C++
            "c#", // C#
            "cfg", "conf", "ini", // configuration files
            "groovy", // Groovy
            "json", "htm", "html", "xhtml", "yaml", // markup languages
            "java", // Java
            "clj", "kt", "scala", // JVM languages
            "js", // Java script
            "lua", // Lua
            "m", "mm", // Objective C
            "php", // PHP
            "pl", "pm", // Perl
            "py", // Python
            "r", // R Language
            "rs", // Rust
            "sh", "bash", "zsh", // Shell programming
            "swift", // Swift
            "sql", "pls" // Database related
    );

    @Override
    public boolean supportsFile(final InputFile path)
    {
        return supportsDefaultFile(path) || supportsConfiguredFile(path);
    }

    private boolean supportsConfiguredFile(final InputFile path)
    {
        return findConfig(path).isPresent();
    }

    boolean supportsDefaultFile(final InputFile file)
    {
        final String path = file.getPath();
        final int lastDotPosition = path.lastIndexOf(".");
        if (lastDotPosition > 0)
        {
            final String extension = path.substring(lastDotPosition + 1);
            return SUPPORTED_DEFAULT_EXTENSIONS.contains(extension);
        }
        else
        {
            return false;
        }
    }

    private Optional<PathConfig> findConfig(final InputFile file)
    {
        return getPathConfigs() //
                .filter(config -> config.matches(file)) //
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
        final ImportSettings settings = getContext().getImportSettings();
        return settings == null ? Stream.empty() : settings.getPathConfigs().stream();
    }
}
