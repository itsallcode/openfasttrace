package org.itsallcode.openfasttrace.api.importer;

import java.util.Objects;

/**
 * Shared base class for {@link ImporterFactory importer factories}.
 */
public abstract class AbstractImporterFactory implements ImporterFactory
{
    private ImporterContext context;

    /**
     * Create a new importer factory base.
     */
    protected AbstractImporterFactory()
    {
        // empty by intention
    }

    @Override
    public void init(final ImporterContext context)
    {
        this.context = context;
    }

    /**
     * Get the {@link ImporterContext} set by the {@link #init(ImporterContext)}
     * method.
     *
     * @return the {@link ImporterContext}.
     */
    public ImporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
