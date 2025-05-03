package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.itsallcode.openfasttrace.api.report.ReporterFactory;

import java.util.logging.Logger;

/**
 * Creates the UX exporter
 */
public class UxReporterFactory extends ReporterFactory {

    private static final String UX_REPORT_FORMAT = "ux";

    public UxReporterFactory() {
    }

    /**
     *
     * @param format to check
     * @return if equal to 'ux'
     */
    @Override public boolean supportsFormat(String format)
    {
        return UX_REPORT_FORMAT.equalsIgnoreCase(format);
    }

    /**
     * Creates the exporter.
     * @param trace the traces to process
     * @return the report
     */
    @Override public Reportable createImporter(Trace trace)
    {
        return new UxReporter(trace,this.getContext());
    }
}
