/**
 * This provides an importer for the SpecObject XML format.
 */
module org.itsallcode.openfasttrace.importer.specobject
{
    exports org.itsallcode.openfasttrace.importer.specobject;
    exports org.itsallcode.openfasttrace.importer.specobject.handler;
    exports org.itsallcode.openfasttrace.importer.specobject.xml;
    exports org.itsallcode.openfasttrace.importer.specobject.xml.tree;
    exports org.itsallcode.openfasttrace.importer.specobject.xml.event;

    requires java.logging;
    requires transitive java.xml;
    requires transitive org.itsallcode.openfasttrace.api;
}
