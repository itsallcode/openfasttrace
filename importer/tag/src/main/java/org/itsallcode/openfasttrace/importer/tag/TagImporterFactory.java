package org.itsallcode.openfasttrace.importer.tag;

import java.util.*;
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
            "c#", "cs", // C#
            "cfg", "conf", "ini", // configuration files
            "go", // Go
            "groovy", // Groovy
            "json", "htm", "html", "xhtml", "yaml", "yml", // markup languages
            "java", // Java
            "clj", "kt", "scala", // JVM languages
            "js", // JavaScript
            "ts", // TypeScript
            "lua", // Lua
            "m", "mm", // Objective C
            "php", // PHP
            "pl", "pm", // Perl
            "py", // Python
            "robot", // Robot Framework
            "pu", "puml", "plantuml", // PlantUML
            "r", // R Language
            "rs", // Rust
            "sh", "bash", "zsh", // Shell programming
            "swift", // Swift
            "tf", "tfvars", // Terraform
            "sql", "pls" // Database related
    );

    /**
     * Create a new {@link TagImporterFactory}.
     */
    public TagImporterFactory()
    {
        // empty by intention
    }

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
                    + DEFAULT_FILE_REGEX + " and " + getPathConfigs().toList());
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
