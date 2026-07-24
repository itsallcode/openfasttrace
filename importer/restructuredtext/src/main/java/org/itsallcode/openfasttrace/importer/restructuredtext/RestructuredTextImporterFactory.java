package org.itsallcode.openfasttrace.importer.restructuredtext;

import java.util.Optional;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;

/**
 * {@link ImporterFactory} for reStructuredText files
 */
public class RestructuredTextImporterFactory extends AbstractRegexMatchingImporterFactory
{
    /** Creates a new instance. */
    public RestructuredTextImporterFactory()
    {
        super("(?i).*\\.rst");
    }

    @Override
    public int getPriority() {
        return 2000;
    }

    @Override
    public Importer createImporter(final InputFile fileName, final ImportEventListener listener)
    {
        return new RestructuredTextImporter(fileName, listener, findConfig(fileName).orElse(null));
    }

    private Optional<PathConfig> findConfig(final InputFile file)
    {
        final ImportSettings settings = getContext().getImportSettings();
        return settings == null ? Optional.empty()
                : settings.getPathConfigs().stream().filter(config -> config.matches(file)).findFirst();
    }
}
