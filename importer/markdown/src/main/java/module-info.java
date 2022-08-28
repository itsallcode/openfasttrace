/**
 * This provides an importer for the MarkDown format.
 * 
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.markdown
{
    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with org.itsallcode.openfasttrace.importer.markdown.MarkdownImporterFactory;
}
