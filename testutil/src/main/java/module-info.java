/**
 * Shared test utilities.
 */
module org.itsallcode.openfasttrace.testutil
{
    exports org.itsallcode.openfasttrace.testutil;
    exports org.itsallcode.openfasttrace.testutil.cli;
    exports org.itsallcode.openfasttrace.testutil.core;
    exports org.itsallcode.openfasttrace.testutil.importer;
    exports org.itsallcode.openfasttrace.testutil.importer.lightweightmarkup;
    exports org.itsallcode.openfasttrace.testutil.log;
    exports org.itsallcode.openfasttrace.testutil.matcher;
    exports org.itsallcode.openfasttrace.testutil.xml;

    requires org.hamcrest;
    requires transitive hamcrest.auto.matcher;
    requires transitive org.junit.jupiter.api;
    requires transitive org.junit.jupiter.params;
    requires transitive org.mockito;
    requires transitive org.mockito.junit.jupiter;
    requires java.logging;
    requires transitive java.xml;
    requires org.itsallcode.openfasttrace.api;
}
