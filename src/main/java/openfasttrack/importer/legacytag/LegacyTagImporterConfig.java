package openfasttrack.importer.legacytag;

import static java.util.Collections.emptyList;

import java.util.List;

public class LegacyTagImporterConfig
{
    private final List<PathConfig> pathConfigs;

    public LegacyTagImporterConfig()
    {
        this(emptyList());
    }

    public LegacyTagImporterConfig(final List<PathConfig> pathConfigs)
    {
        this.pathConfigs = pathConfigs;
    }

    public List<PathConfig> getPathConfigs()
    {
        return this.pathConfigs;
    }
}
