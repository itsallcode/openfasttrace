/**
 * This provides an exporter for the SpecObject XML format.
 * 
 * @provides org.itsallcode.openfasttrace.api.exporter.ExporterFactory
 */
module org.itsallcode.openfasttrace.exporter.specobject
{
    requires java.logging;
    requires java.xml;
    requires org.itsallcode.openfasttrace.api;
    requires org.itsallcode.openfasttrace.exporter.common;

    provides org.itsallcode.openfasttrace.api.exporter.ExporterFactory
            with org.itsallcode.openfasttrace.exporter.specobject.SpecobjectExporterFactory;
}
