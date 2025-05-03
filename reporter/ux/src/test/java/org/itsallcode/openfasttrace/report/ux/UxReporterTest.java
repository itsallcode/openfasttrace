package org.itsallcode.openfasttrace.report.ux;

import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.api.report.Reportable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.itsallcode.openfasttrace.report.ux.TestHelper.createReporter;
import static org.itsallcode.openfasttrace.report.ux.TestHelper.createTrace;

class UxReporterTest
{
    @Test
    void generatedModelContainsProjectNameFromProperty()
    {
        System.setProperty("oftProjectName", "TestProject");
        final Reportable reporter = createReporter(SampleData.LINKED_SAMPLE_ITEMS);
        final OutputStream outputStream = new ByteArrayOutputStream();
        reporter.renderToStream(outputStream);
        final String output = outputStream.toString();
        assertThat(output, matchesPattern("(?s).*projectName *: *['\"]TestProject.*"));
    }
}