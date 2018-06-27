package org.itsallcode.openfasttrace.mode;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.Reporter;
import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.core.Trace;
import org.itsallcode.openfasttrace.report.ReportConstants;
import org.itsallcode.openfasttrace.report.ReportVerbosity;
import org.junit.Before;
import org.junit.Test;

public class ITestReporter extends AbstractOftModeTest
{
    private Reporter reporter;

    @Before
    public void setUp() throws UnsupportedEncodingException
    {
        perpareOutput();
        this.reporter = new ReportMode();
    }

    @Test
    public void testTraceToFile() throws IOException
    {
        final Trace trace = this.reporter.addInputs(this.docDir) //
                .trace();
        writePlainTextReportFromTrace(trace);
        assertStandardReportFileResult();
    }

    private void writePlainTextReportFromTrace(final Trace trace)
    {
        this.reporter.reportToFileInFormat(trace, this.outputFile,
                ReportConstants.DEFAULT_REPORT_FORMAT);
    }

    private void assertStandardReportFileResult() throws IOException
    {
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith("ok - 5 total");
    }

    @Test
    public void testTraceWithReportVerbosityMinimal() throws IOException
    {
        final Trace trace = this.reporter.addInputs(this.docDir) //
                .setReportVerbosity(ReportVerbosity.MINIMAL) //
                .trace();
        writePlainTextReportFromTrace(trace);
        assertOutputFileExists(true);
        assertOutputFileContentStartsWith("ok");
    }

    @Test
    public void testTraceMacNewlines() throws IOException
    {
        final Trace trace = this.reporter.addInputs(this.docDir) //
                .setNewline(Newline.OLDMAC) //
                .trace();
        writePlainTextReportFromTrace(trace);
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat("Has old Mac newlines", getOutputFileContent().contains(CARRIAGE_RETURN),
                equalTo(true));
        assertThat("Has no Unix newlines", getOutputFileContent().contains(NEWLINE),
                equalTo(false));
    }

    @Test
    public void testTraceToStdOut() throws IOException
    {
        final Trace trace = this.reporter.addInputs(this.docDir) //
                .trace();
        this.reporter.reportToStdOutInFormat(trace, ReportConstants.DEFAULT_REPORT_FORMAT);
        assertStandardReportStdOutResult();
    }

    private void assertStandardReportStdOutResult() throws IOException
    {
        assertStdOutStartsWith("ok - 5 total");
    }

    // [itest->dsn~filtering-by-artifact-types-during-import~1]
    @Test
    public void testFilterAllowsAllAtrifactsButDsn()
    {
        final Set<String> artifactTypes = new HashSet<>(Arrays.asList("feat", "req"));
        this.reporter.addInputs(this.docDir);
        final Trace fullTrace = this.reporter.trace();
        assertThat("Number of items with type \"dsn\" in regular trace",
                countItemsOfArtifactTypeInTrace("dsn", fullTrace), greaterThan(0L));
        final Trace trace = this.reporter //
                .setFilters(new FilterSettings.Builder().artifactTypes(artifactTypes).build())//
                .trace();
        assertThat("Number of items with ignored type \"dsn\" in filtered trace",
                countItemsOfArtifactTypeInTrace("dsn", trace), equalTo(0L));
    }

    private long countItemsOfArtifactTypeInTrace(final String artifactType, final Trace trace)
    {
        return trace.getItems().stream() //
                .filter(item -> {
                    return item.getId().getArtifactType().equals(artifactType);
                }) //
                .count();
    }

    // [itest->dsn~filtering-by-tags-during-import~1]
    @Test
    public void testFilterAllowsTagsClientAndServer()
    {
        final Set<String> tags = new HashSet<>(Arrays.asList("client", "server"));
        this.reporter.addInputs(this.docDir);
        final Trace fullTrace = this.reporter.trace();
        final String filteredOutTag = "database";
        assertThat("Number of items with tag \"" + filteredOutTag + "\" in regular trace",
                countItemsWithTagInTrace(filteredOutTag, fullTrace), greaterThan(0L));
        final Trace trace = this.reporter //
                .setFilters(new FilterSettings.Builder().tags(tags).build())//
                .trace();
        assertThat("Number of items with tag \"" + filteredOutTag + "\" in filted trace",
                countItemsWithTagInTrace(filteredOutTag, trace), equalTo(0L));
    }

    private long countItemsWithTagInTrace(final String tag, final Trace trace)
    {
        return trace.getItems().stream() //
                .filter(item -> {
                    return item.getTags().contains(tag);
                }) //
                .count();
    }
}
