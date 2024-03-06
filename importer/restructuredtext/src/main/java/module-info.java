import org.itsallcode.openfasttrace.importer.restructuredtext.RestructuredTextImporterFactory;

/**
 * This provides an importer for the reStructuredText format.
 * 
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.restructuredtext
{
    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.importer.lightweightmarkup;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with RestructuredTextImporterFactory;
}
