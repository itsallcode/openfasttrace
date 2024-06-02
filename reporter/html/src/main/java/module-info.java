/**
 * This provides a report generator for the HTML format.
 * 
 * @provides org.itsallcode.openfasttrace.api.report.ReporterFactory
 */
module org.itsallcode.openfasttrace.report.html
{
    requires transitive org.itsallcode.openfasttrace.api;

    provides org.itsallcode.openfasttrace.api.report.ReporterFactory
            with org.itsallcode.openfasttrace.report.html.HtmlReporterFactory;
}
