package org.itsallcode.openfasttrace.report.aspec;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

/**
 * A {@link ReporterFactory} for SpecObject reports. This supports the
 * <code>specobject</code> format.
 */
public class ASpecReporterFactory extends ReporterFactory
{
    private static final String ASPEC_REPORT_FORMAT = "aspec";

    /**
     * Create a new {@link ASpecReporterFactory}.
     */
    public ASpecReporterFactory()
    {
        // empty by intention
    }

    @Override
    public boolean supportsFormat(String format)
    {
        return ASPEC_REPORT_FORMAT.equalsIgnoreCase(format);
    }

    @Override
    public Reportable createImporter(Trace trace)
    {
        return new ASpecReport(trace, getContext());
    }

}
