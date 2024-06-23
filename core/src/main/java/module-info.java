/**
 * The Core OpenFastTrace module.
 * 
 * @uses org.itsallcode.openfasttrace.api.report.ReporterFactory
 * @uses org.itsallcode.openfasttrace.api.importer.ImporterFactory
 * @uses org.itsallcode.openfasttrace.api.exporter.ExporterFactory
 */
module org.itsallcode.openfasttrace.core
{
    exports org.itsallcode.openfasttrace.core;
    exports org.itsallcode.openfasttrace.core.cli;
    exports org.itsallcode.openfasttrace.core.cli.commands;
    exports org.itsallcode.openfasttrace.core.cli.logging;
    exports org.itsallcode.openfasttrace.core.report;
    exports org.itsallcode.openfasttrace.core.exporter;
    exports org.itsallcode.openfasttrace.core.importer;
    exports org.itsallcode.openfasttrace.core.serviceloader;

    requires transitive java.logging;
    requires transitive org.itsallcode.openfasttrace.api;

    uses org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
    uses org.itsallcode.openfasttrace.api.importer.ImporterFactory;
    uses org.itsallcode.openfasttrace.api.report.ReporterFactory;
}
