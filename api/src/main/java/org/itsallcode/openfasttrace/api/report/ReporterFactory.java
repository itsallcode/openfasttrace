package org.itsallcode.openfasttrace.api.report;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;

/**
 * Interface for factories producing {@link Reportable}s.
 */
public interface ReporterFactory extends Initializable<ReporterContext>
{
    /**
     * Check if this {@link ReporterFactory} supports creating
     * {@link Reportable}s for the given format.
     * 
     * @param format
     *            the format to check.
     * @return {@code true} if this {@link ReporterFactory} supports the given
     *         format.
     */
    boolean supportsFormat(final String format);

    /**
     * Create a new {@link Reportable}.
     *
     * @param trace
     *            the trace that will be reported.
     * @return the new {@link Reportable}.
     */
    Reportable createImporter(final Trace trace);

    /**
     * Get the {@link ReporterContext}.
     * 
     * @return the {@link ReporterContext}.
     */
    ReporterContext getContext();
}
