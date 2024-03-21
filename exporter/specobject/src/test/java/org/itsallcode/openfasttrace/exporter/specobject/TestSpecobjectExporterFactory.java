package org.itsallcode.openfasttrace.exporter.specobject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.itsallcode.openfasttrace.testutil.core.ItemBuilderFactory.item;

import java.io.StringWriter;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.exporter.Exporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestSpecobjectExporterFactory
{
    private SpecobjectExporterFactory factory;

    @BeforeEach
    void setUp()
    {
        factory = new SpecobjectExporterFactory();
    }

    @Test
    void testCreateExporterWriterStreamOfSpecificationItemNewline()
    {
        final StringWriter writer = new StringWriter();
        final SpecificationItem item = item().id("art", "name", 42).build();

        final Exporter exporter = factory.createExporter(writer, Stream.of(item), Newline.UNIX);
        exporter.runExport();

        assertThat(writer.getBuffer().toString(), equalTo(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<specdocument>\n  <specobjects doctype=\"art\">\n    <specobject>\n      <id>name</id>\n      <status>approved</status>\n      <version>42</version>\n    </specobject>\n  </specobjects>\n</specdocument>"));
    }
}
