package openfasttrack.report;

/**
 * The {@link ReportVerbosity} is used to control the detail level of coverage
 * reports.
 */
public enum ReportVerbosity
{
    //@formatter:off
    /** No output, only set exit state */      QUIET,
    /** Output "OK", "FAIL" only */            MINIMAL,
    /** Summary only */                        SUMMARY,
    /** List of unclean specification items */ FAILURES,
    /** Details of unclean items */            FAILURE_DETAILS,
    /** Details for all items */               ALL
    //@formatter:on
}
