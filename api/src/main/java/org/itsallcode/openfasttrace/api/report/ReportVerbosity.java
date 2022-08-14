package org.itsallcode.openfasttrace.api.report;

/**
 * The {@link ReportVerbosity} is used to control the detail level of coverage
 * reports.
 */
public enum ReportVerbosity
{
    //@formatter:off
    /** No output, only set exit state */           QUIET,
    /** Output "OK", "FAIL" only */                 MINIMAL,
    /** Summary only */                             SUMMARY,
    /** List of defect specification items */       FAILURES,
    /** Summaries of defect specification items */  FAILURE_SUMMARIES,
    /** Details of unclean items */                 FAILURE_DETAILS,
    /** Details for all items */                    ALL
    //@formatter:on
}
