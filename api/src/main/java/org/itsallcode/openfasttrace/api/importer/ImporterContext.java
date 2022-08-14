package org.itsallcode.openfasttrace.api.importer;
import java.util.Objects;

/**
 * Common context shared by all {@link ImporterFactory}s. This allows importers
 * to access common infrastructure, e.g. the {@link ImporterService}.
 */
public class ImporterContext
{
    private final ImportSettings settings;
    private ImporterService importerService;

    /**
     * Creates a new {@link ImporterContext}.
     * 
     * @param settings
     *            importer specific configuration.
     */
    public ImporterContext(final ImportSettings settings)
    {
        this.settings = settings;
    }

    /**
     * Internal API used by the {@link ImporterService}.
     * 
     * @param importerService
     *            the common instance of {@link ImporterService}.
     */
    public void setImporterService(final ImporterService importerService)
    {
        this.importerService = importerService;
    }

    /**
     * Get the common instance of {@link ImporterService}. Can be used e.g. for
     * delegating to other importers.
     * 
     * @return the common instance of {@link ImporterService}.
     */
    public ImporterService getImporterService()
    {
        return Objects.requireNonNull(this.importerService, "Importer service was not initialized");
    }

    /**
     * Get importer specific configuration.
     * 
     * @return importer specific configuration.
     */
    public ImportSettings getImportSettings()
    {
        return this.settings;
    }
}