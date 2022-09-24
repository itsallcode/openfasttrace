/**
 * This provides an report generator for the plain text format.
 * 
 * @provides org.itsallcode.openfasttrace.api.report.ReporterFactory
 */
module org.itsallcode.openfasttrace.report.plaintext
{
    requires transitive org.itsallcode.openfasttrace.api;

    provides org.itsallcode.openfasttrace.api.report.ReporterFactory
            with org.itsallcode.openfasttrace.report.plaintext.PlaintextReporterFactory;
}
