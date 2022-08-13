package org.itsallcode.openfasttrace.report.html;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

/**
 * A {@link ReporterFactory} for HTML reports. This supports the
 * <code>html</code> format.
 */
public class HtmlReporterFactory extends ReporterFactory
{
    private static final String HTML_REPORT_FORMAT = "html";

    @Override
    public boolean supportsFormat(String format)
    {
        return HTML_REPORT_FORMAT.equalsIgnoreCase(format);
    }

    @Override
    public Reportable createImporter(Trace trace)
    {
        return new HtmlReport(trace);
    }
}
