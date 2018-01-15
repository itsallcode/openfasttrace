package openfasttrack.importer.legacytag;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import openfasttrack.importer.ImportEventListener;
import openfasttrack.importer.Importer;
import openfasttrack.importer.ImporterException;
import openfasttrack.importer.ImporterFactory;

public class LegacyTagImporterFactory extends ImporterFactory
{
    private static final Logger LOG = Logger
            .getLogger(LegacyTagImporterFactory.class.getName());

    private final List<PathConfig> pathConfigs;

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
            throw new ImporterException("File '" + file + "' not supported for import");
        }
        LOG.finest(() -> "Creating importer for file " + file);
        final BufferedReader reader = createReader(file, charset);
        return new LegacyTagImporter(config.get(), file, reader, listener);
    }
}
