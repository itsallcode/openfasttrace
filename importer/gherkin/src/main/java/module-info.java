import org.itsallcode.openfasttrace.importer.gherkin.GherkinImporterFactory;

/**
 * Provides an importer for Gherkin {@code .feature} files.
 *
 * @provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
 */
module org.itsallcode.openfasttrace.importer.gherkin
{
    requires java.logging;
    requires transitive org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.importer.tag.common;

    provides org.itsallcode.openfasttrace.api.importer.ImporterFactory
            with GherkinImporterFactory;
}
