package org.itsallcode.openfasttrace.report.plaintext;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

/**
 * A {@link ReporterFactory} for plain text reports. This supports the
 * <code>plain</code> format.
 */
public class PlaintextReporterFactory extends ReporterFactory
{
    private static final String PLAIN_REPORT_FORMAT = "plain";

    /**
     * Create a new {@link PlaintextReporterFactory}.
     */
    public PlaintextReporterFactory()
    {
        // empty by intention
    }

    @Override
    public boolean supportsFormat(String format)
    {
        return PLAIN_REPORT_FORMAT.equalsIgnoreCase(format);
    }

    @Override
    public Reportable createImporter(Trace trace)
    {
        return new PlainTextReport(trace, getContext().getSettings());
    }
}
