package org.itsallcode.openfasttrace.api.report;

/**
 * Common constants for reports.
 */
public final class ReportConstants
{
    /**
     * The default report format.
     */
    public static final String DEFAULT_REPORT_FORMAT = "plain";
    /**
     * The default report verbosity.
     */
    public static final ReportVerbosity DEFAULT_REPORT_VERBOSITY = ReportVerbosity.FAILURE_DETAILS;

    // Prevent class from being instantiated.
    private ReportConstants()
    {
    }
}
