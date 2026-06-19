package org.itsallcode.openfasttrace.api.report;

import java.util.Objects;

/**
 * Base class for {@link ReporterFactory} implementations that share context handling.
 */
public abstract class AbstractReporterFactory implements ReporterFactory
{
    private ReporterContext context;

    /**
     * Create a new {@link AbstractReporterFactory}.
     */
    protected AbstractReporterFactory()
    {
        // empty by intention
    }

    @Override
    public void init(final ReporterContext context)
    {
        this.context = context;
    }

    /**
     * Get the {@link ReporterContext} set by the {@link #init(ReporterContext)}
     * method.
     *
     * @return the {@link ReporterContext}.
     */
    @Override
    public ReporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
