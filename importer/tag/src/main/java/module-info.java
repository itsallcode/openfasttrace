/**
 * This provides an importer for coverage tags.
 *
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.tag
{
    requires transitive org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.importer.tag.common;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with org.itsallcode.openfasttrace.importer.tag.TagImporterFactory;
}
