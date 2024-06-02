import org.itsallcode.openfasttrace.importer.markdown.MarkdownImporterFactory;

/**
 * This provides an importer for the Markdown format.
 * 
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.markdown
{
    requires transitive org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.importer.lightweightmarkup;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with MarkdownImporterFactory;
}
