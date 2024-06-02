/**
 * Base module for importers of lightweight markup formats.
 */
module org.itsallcode.openfasttrace.importer.lightweightmarkup
{
    exports org.itsallcode.openfasttrace.importer.lightweightmarkup;
    exports org.itsallcode.openfasttrace.importer.lightweightmarkup.statemachine;
    exports org.itsallcode.openfasttrace.importer.lightweightmarkup.linereader;

    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;
}
