/**
 * This provides an report generator for the Aspec format.
 * 
 * @provides org.itsallcode.openfasttrace.api.report.ReporterFactory
 */
module org.itsallcode.openfasttrace.report.aspec
{
    requires java.xml;
    requires java.logging;
    requires org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.exporter.common;

    provides org.itsallcode.openfasttrace.api.report.ReporterFactory
            with org.itsallcode.openfasttrace.report.aspec.ASpecReporterFactory;
}
