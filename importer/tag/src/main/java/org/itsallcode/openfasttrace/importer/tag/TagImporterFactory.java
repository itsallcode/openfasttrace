package org.itsallcode.openfasttrace.importer.tag;

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

    public boolean supportsDefaultFile(final InputFile file)
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
