module org.itsallcode.openfasttrace.core
{
    exports org.itsallcode.openfasttrace.core.cli.commands;
    exports org.itsallcode.openfasttrace.core.report;
    exports org.itsallcode.openfasttrace.core.cli;
    exports org.itsallcode.openfasttrace.core.exporter;
    exports org.itsallcode.openfasttrace.core.importer;
    exports org.itsallcode.openfasttrace.core.serviceloader;
    exports org.itsallcode.openfasttrace.core;

    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;

    uses org.itsallcode.openfasttrace.api.exporter.ExporterFactory;
    uses org.itsallcode.openfasttrace.api.importer.ImporterFactory;
    uses org.itsallcode.openfasttrace.api.report.ReporterFactory;
}
