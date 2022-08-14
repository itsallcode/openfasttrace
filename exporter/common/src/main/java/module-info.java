/**
 * Common classes for XML exporters.
 */
module org.itsallcode.openfasttrace.exporter.common
{
    exports org.itsallcode.openfasttrace.exporter.common;

    requires java.logging;
    requires transitive java.xml;
    requires org.itsallcode.openfasttrace.api;
}
