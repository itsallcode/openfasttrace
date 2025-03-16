package org.itsallcode.openfasttrace.report.ux;

import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.itsallcode.openfasttrace.report.ux.TestHelper.createReporter;
import static org.junit.jupiter.api.Assertions.*;

class UxReporterFactoryTest
{
    @Test
    public void testFormat()
    {
        final UxReporterFactory factory = new UxReporterFactory();
        assertTrue(factory.supportsFormat("ux"));
        assertFalse(factory.supportsFormat("plain"));
        assertFalse(factory.supportsFormat("html"));
        assertFalse(factory.supportsFormat("aspec"));
    }

    @Test
    void factoryCreatesUxReporter()
    {
        final Reportable reporter = createReporter(SampleData.LINKED_SAMPLE_ITEMS);
        assertThat(reporter, instanceOf(UxReporter.class));
    }

}