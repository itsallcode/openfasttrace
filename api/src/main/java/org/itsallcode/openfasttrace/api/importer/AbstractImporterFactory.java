package org.itsallcode.openfasttrace.api.importer;

import java.util.Objects;

/**
 * Base class for {@link ImporterFactory} implementations that share context handling.
 */
public abstract class AbstractImporterFactory implements ImporterFactory
{
    private ImporterContext context;

    /**
     * Create a new {@link AbstractImporterFactory}.
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
    @Override
    public ImporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
