package org.itsallcode.openfasttrace.api.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

import org.itsallcode.openfasttrace.api.core.Trace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestReporterFactory
{
    private ReporterFactory reporterFactory;
    private ReporterContext context;

    @BeforeEach
    void setUp()
    {
        context = new ReporterContext(null);
        reporterFactory = new TestingReporterFactory();
    }

    @Test
    void testInitSetsContext()
    {
        reporterFactory.init(context);
        assertThat(reporterFactory.getContext(), sameInstance(context));
    }

    private static class TestingReporterFactory extends ReporterFactory
    {
        @Override
        public boolean supportsFormat(String format)
        {
            return false;
        }

        @Override
        public Reportable createImporter(Trace trace)
        {
            return null;
        }
    }
}
