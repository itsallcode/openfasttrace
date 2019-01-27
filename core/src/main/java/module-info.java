module openfasttrace
{
    exports org.itsallcode.openfasttrace;
    exports org.itsallcode.openfasttrace.core;
    exports org.itsallcode.openfasttrace.importer.tag.config;
    exports org.itsallcode.openfasttrace.report;
    exports org.itsallcode.openfasttrace.exporter;
    exports org.itsallcode.openfasttrace.importer.specobject;
    exports org.itsallcode.openfasttrace.core.xml;
    exports org.itsallcode.openfasttrace.core.xml.tree;
    exports org.itsallcode.openfasttrace.cli;
    exports org.itsallcode.openfasttrace.importer;
    exports org.itsallcode.openfasttrace.importer.input;
    exports org.itsallcode.openfasttrace.report.html;
    exports org.itsallcode.openfasttrace.report.plaintext;
    exports org.itsallcode.openfasttrace.importer.zip;
    exports org.itsallcode.openfasttrace.cli.commands;
    exports org.itsallcode.openfasttrace.core.xml.event;
    exports org.itsallcode.openfasttrace.importer.markdown;
    exports org.itsallcode.openfasttrace.report.view;
    exports org.itsallcode.openfasttrace.importer.tag;
    exports org.itsallcode.openfasttrace.report.view.html;
    exports org.itsallcode.openfasttrace.exporter.specobject;
    exports org.itsallcode.openfasttrace.core.serviceloader;

    requires java.compiler;
    requires java.logging;
    requires java.xml;
}