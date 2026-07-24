package org.itsallcode.openfasttrace.importer.markdown;

import java.util.Optional;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;

/**
 * {@link ImporterFactory} for Markdown files
 */
public class MarkdownImporterFactory extends AbstractRegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public MarkdownImporterFactory()
    {
        super("(?i).*\\.markdown", "(?i).*\\.md");
    }

    @Override
    public int getPriority()
    {
        return 1000;
    }

    @Override
    public Importer createImporter(final InputFile fileName, final ImportEventListener listener)
    {
        return new MarkdownImporter(fileName, listener, findConfig(fileName).orElse(null));
    }

    private Optional<PathConfig> findConfig(final InputFile file)
    {
        final ImportSettings settings = getContext().getImportSettings();
        return settings == null ? Optional.empty()
                : settings.getPathConfigs().stream().filter(config -> config.matches(file)).findFirst();
    }
}
