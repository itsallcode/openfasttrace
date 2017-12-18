package openfasttrack.cli;

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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import openfasttrack.Converter;
import openfasttrack.Reporter;
import openfasttrack.cli.commands.ConvertCommand;
import openfasttrack.cli.commands.TraceCommand;
import openfasttrack.core.Newline;
import openfasttrack.report.ReportVerbosity;

public class TestCliArguments
{
    private static final String AFTER_SETTER = "after setter";
    private static final String BEFORE_SETTER = "before setter";
    private CliArguments arguments;

    @Before
    public void testSetUp()
    {
        this.arguments = new CliArguments();
    }

    @Test
    public void testGetCommandWithUnnamedValuesNull()
    {
        this.arguments.setUnnamedValues(null);
        assertThat(this.arguments.getCommand(), isEmptyOrNullString());
    }

    @Test
    public void testGetCommandWithUnnamedValuesEmpty()
    {
        this.arguments.setUnnamedValues(emptyList());
        assertThat(this.arguments.getCommand(), isEmptyOrNullString());
    }

    @Test
    public void testSetOutputFormat()
    {
        final String value = "foobar";
        assertThat(BEFORE_SETTER, this.arguments.getOutputFormat(), equalTo(null));
        this.arguments.setOutputFormat(value);
        assertAfterSetter(value, this.arguments.getOutputFormat());
    }

    // [utest->dsn~cli.conversion.default-format~1]
    @Test
    public void getStandardOutputFormatForExport()
    {
        this.arguments.setUnnamedValues(asList(ConvertCommand.COMMAND_NAME));
        assertThat(this.arguments.getOutputFormat(), equalTo(Converter.DEFAULT_EXPORT_FORMAT));
    }

    // [utest->dsn~cli.tracing.default-format~1]
    @Test
    public void getStandardOutputFormatForReport()
    {
        this.arguments.setUnnamedValues(asList(TraceCommand.COMMAND_NAME));
        assertThat(this.arguments.getOutputFormat(), equalTo(Reporter.DEFAULT_REPORT_FORMAT));
    }

    @Test
    public void testSetO()
    {
        final String value = "foobar";
        assertThat(BEFORE_SETTER, this.arguments.getOutputFormat(), equalTo(null));
        this.arguments.setO(value);
        assertAfterSetter(value, this.arguments.getOutputFormat());
    }

    private void assertAfterSetter(final String value, final String outputFormat)
    {
        assertThat(AFTER_SETTER, outputFormat, equalTo(value));
    }

    @Test
    public void testSetOutputFile()
    {
        final String value = "/tmp/foobar";
        final String expectedPath = Paths.get(value).toString();
        assertThat(BEFORE_SETTER, this.arguments.getOutputPath(), equalTo(null));
        this.arguments.setOutputFile(value);
        assertThat(AFTER_SETTER, this.arguments.getOutputPath().toString(), equalTo(expectedPath));
    }

    @Test
    public void testSetF()
    {
        final String value = "/tmp/foobar";
        final String expectedPath = Paths.get(value).toString();
        assertThat(BEFORE_SETTER, this.arguments.getOutputPath(), equalTo(null));
        this.arguments.setF(value);
        assertThat(AFTER_SETTER, this.arguments.getOutputPath().toString(), equalTo(expectedPath));
    }

    @Test
    public void testSetReportVerbositiy()
    {
        final ReportVerbosity value = ReportVerbosity.QUIET;
        assertThat(BEFORE_SETTER, this.arguments.getReportVerbosity(),
                equalTo(Reporter.DEFAULT_VERBOSITY));
        this.arguments.setReportVerbosity(value);
        assertThat(AFTER_SETTER, this.arguments.getReportVerbosity(), equalTo(value));
    }

    @Test
    public void testSetV()
    {
        final ReportVerbosity value = ReportVerbosity.QUIET;
        assertThat(BEFORE_SETTER, this.arguments.getReportVerbosity(),
                equalTo(Reporter.DEFAULT_VERBOSITY));
        this.arguments.setV(value);
        assertThat(AFTER_SETTER, this.arguments.getReportVerbosity(), equalTo(value));
    }

    @Test
    public void testSetNewline()
    {
        final Newline value = Newline.OLDMAC;
        this.arguments.setNewline(value);
        assertThat(AFTER_SETTER, this.arguments.getNewline(), equalTo(value));
    }

    @Test
    public void testSetN()
    {
        final Newline value = Newline.OLDMAC;
        this.arguments.setN(value);
        assertThat(AFTER_SETTER, this.arguments.getNewline(), equalTo(value));
    }
}
