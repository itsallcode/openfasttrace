/**
 * The general OpenFastTrace API used by all modules.
 */
module org.itsallcode.openfasttrace.api
{
    exports org.itsallcode.openfasttrace.api;
    exports org.itsallcode.openfasttrace.api.core;
    exports org.itsallcode.openfasttrace.api.core.serviceloader;
    exports org.itsallcode.openfasttrace.api.cli;
    exports org.itsallcode.openfasttrace.api.importer;
    exports org.itsallcode.openfasttrace.api.importer.input;
    exports org.itsallcode.openfasttrace.api.importer.tag.config;
    exports org.itsallcode.openfasttrace.api.exporter;
    exports org.itsallcode.openfasttrace.api.report;

    requires java.compiler;
    requires java.logging;
}
