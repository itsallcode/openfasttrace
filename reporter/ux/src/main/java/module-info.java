/**
 * This provides an interactive HTML requirement browser.
 *
 * @provides org.itsallcode.openfasttrace.api.report.ReporterFactory
 */
module org.itsallcode.openfasttrace.report.ux
{
    requires transitive org.itsallcode.openfasttrace.api;
    requires java.logging;
    requires java.desktop;
    requires jdk.jfr;
    requires java.xml.crypto;
    requires java.xml;

    provides org.itsallcode.openfasttrace.api.report.ReporterFactory
            with org.itsallcode.openfasttrace.report.ux.UxReporterFactory;
}
