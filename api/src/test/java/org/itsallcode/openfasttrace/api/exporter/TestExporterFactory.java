package org.itsallcode.openfasttrace.api.exporter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

import java.io.Writer;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestExporterFactory
{
    private ExporterFactory exporterFactory;
    private ExporterContext context;

    @BeforeEach
    void setUp()
    {
        context = new ExporterContext();
        exporterFactory = new TestingExporterFactory();
    }

    @Test
    void testInitSetsContext()
    {
        exporterFactory.init(context);
        assertThat(exporterFactory.getContext(), sameInstance(context));
    }

    private static class TestingExporterFactory extends AbstractExporterFactory
    {
        TestingExporterFactory()
        {
            super("test");
        }

        @Override
        public Exporter createExporter(final Writer writer,
                final Stream<SpecificationItem> linkedSpecItemStream, final Newline newline)
        {
            return () -> {
                // empty by intention
            };
        }
    }
}
