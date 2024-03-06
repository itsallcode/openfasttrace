/**
 * This provides a simple XML parser based on element handlers.
 */
module org.itsallcode.openfasttrace.importer.xmlparser {
    requires java.logging;
    requires transitive java.xml;
    requires transitive org.itsallcode.openfasttrace.api;

    exports org.itsallcode.openfasttrace.importer.xmlparser;
    exports org.itsallcode.openfasttrace.importer.xmlparser.event;
    exports org.itsallcode.openfasttrace.importer.xmlparser.tree;
}
