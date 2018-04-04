package org.itsallcode.openfasttrace.importer.legacytag;

import static java.util.Collections.emptyList;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Configuration for the {@link LegacyTagImporter}.
 */
public class LegacyTagImporterConfig
{
    private final List<PathConfig> pathConfigs;
    private final Optional<Path> basePath;

    /**
     * Create a new configuration object.
     * 
     * @param basePath
     *            the root directory of the project to import. All paths will be
     *            relative to this path.
     * @param pathConfigs
     *            a list of {@link PathConfig} objects.
     */
    public LegacyTagImporterConfig(final Optional<Path> basePath,
            final List<PathConfig> pathConfigs)
    {
        this.basePath = basePath;
        this.pathConfigs = pathConfigs;
    }

    /**
     * Creates a new, empty configuration.
     */
    public static LegacyTagImporterConfig empty()
    {
        return new LegacyTagImporterConfig(Optional.empty(), emptyList());
    }

    public List<PathConfig> getPathConfigs()
    {
        return this.pathConfigs;
    }

    public Optional<Path> getBasePath()
    {
        return this.basePath;
    }
}
