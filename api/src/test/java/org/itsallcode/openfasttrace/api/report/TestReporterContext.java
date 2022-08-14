package org.itsallcode.openfasttrace.api.report;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.junit.jupiter.api.Test;

class TestReporterContext
{
    @Test
    void testGetSettings()
    {
        final ReportSettings settings = ReportSettings.createDefault();
        assertThat(new ReporterContext(settings).getSettings(), sameInstance(settings));
    }
}
