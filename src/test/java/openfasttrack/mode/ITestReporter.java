package openfasttrack.mode;

/*-
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.Reporter;
import openfasttrack.core.Newline;
import openfasttrack.core.Trace;
import openfasttrack.report.ReportVerbosity;

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
        this.reporter.reportToFileInFormat(trace, this.outputFile, Reporter.DEFAULT_REPORT_FORMAT);
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
        this.reporter.reportToStdOutInFormat(trace, Reporter.DEFAULT_REPORT_FORMAT);
        assertStandardReportStdOutResult();
    }

    private void assertStandardReportStdOutResult() throws IOException
    {
        assertStdOutStartsWith("ok - 5 total");
    }
}
