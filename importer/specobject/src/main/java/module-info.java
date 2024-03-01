/**
 * This provides an importer for the SpecObject XML format.
 * 
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.specobject
{
    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;
    requires transitive org.itsallcode.openfasttrace.importer.xmlparser;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with org.itsallcode.openfasttrace.importer.specobject.SpecobjectImporterFactory;
}
