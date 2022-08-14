module org.itsallcode.openfasttrace.importer.specobject
{
    exports org.itsallcode.openfasttrace.importer.specobject.xml.tree;
    exports org.itsallcode.openfasttrace.importer.specobject.handler;
    exports org.itsallcode.openfasttrace.importer.specobject.xml.event;
    exports org.itsallcode.openfasttrace.importer.specobject;
    exports org.itsallcode.openfasttrace.importer.specobject.xml;

    requires java.logging;
    requires transitive java.xml;
    requires transitive org.itsallcode.openfasttrace.api;
}
