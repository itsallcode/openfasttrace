/**
 * Shared line scanning and coverage-tag parsing support for importers.
 */
module org.itsallcode.openfasttrace.importer.tag.common {
    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;

    exports org.itsallcode.openfasttrace.importer.tag.common;
}
