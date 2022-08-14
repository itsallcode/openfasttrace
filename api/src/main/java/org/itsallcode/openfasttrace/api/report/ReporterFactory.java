package org.itsallcode.openfasttrace.api.report;
import java.util.Objects;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;

/**
 * Super class for factories producing {@link Reportable}s.
 */
public abstract class ReporterFactory implements Initializable<ReporterContext>
{
    private ReporterContext context;

    /**
     * Check if this {@link ReporterFactory} supports creating
     * {@link Reportable}s for the given format.
     * 
     * @param format
     *            the format to check.
     * @return <code>true</code> if this {@link ReporterFactory} supports the
     *         given format.
     */
    public abstract boolean supportsFormat(final String format);

    /**
     * Create a new {@link Reportable}.
     *
     * @param trace
     *            the trace that will be reported.
     * @return the new {@link Reportable}.
     */
    public abstract Reportable createImporter(final Trace trace);

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
    public ReporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
