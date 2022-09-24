/**
 * This provides an importer for the ZIP files.
 * 
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.zip
{
    requires transitive org.itsallcode.openfasttrace.api;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with org.itsallcode.openfasttrace.importer.zip.ZipFileImporterFactory;
}
