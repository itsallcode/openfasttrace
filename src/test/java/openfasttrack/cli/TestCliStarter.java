package openfasttrack.cli;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import openfasttrack.exporter.ExporterException;

public class TestCliStarter
{
    private static final String CONVERT_COMMAND = "convert";
    private static final String TRACE_COMMAND = "trace";
    private static final String INPUT_DIR_PARAMETER = "--input-dir";
    private static final String OUTPUT_FILE_PARAMETER = "--output-file";
    private static final String REPORT_VERBOSITY_PARAMETER = "--report-verbosity";
    private static final String OUTPUT_FORMAT_PARAMETER = "--output-format";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private Path docDir;
    private Path outputFile;

    @Before
    public void setUp()
    {
        this.docDir = Paths.get("doc").toAbsolutePath();
        this.outputFile = this.tempFolder.getRoot().toPath().resolve("report.txt");
    }

    @Test
    public void testNoArg()
    {
        expectException(asList(), CliException.class, "No command given");
    }

    @Test
    public void testIllegalCommand()
    {
        expectException(asList("illegal"), CliException.class, "Invalid command 'illegal' given");
    }

    @Test
    public void testConvertNoArg()
    {
        runCliStarter(asList(CONVERT_COMMAND));
        assertThat(Files.exists(this.outputFile), equalTo(false));
    }

    @Test
    public void testConvertUnknownExporter()
    {
        expectException(
                asList(CONVERT_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                        OUTPUT_FORMAT_PARAMETER, "illegal", OUTPUT_FILE_PARAMETER,
                        this.outputFile.toString()),
                ExporterException.class, "Found no matching exporter for output format 'illegal'");
    }

    @Test
    public void testConvertToSpecobject() throws IOException
    {
        runCliStarter(asList(CONVERT_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FORMAT_PARAMETER, "specobject", OUTPUT_FILE_PARAMETER,
                this.outputFile.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile).length(), greaterThan(10000));
    }

    @Test
    public void testConvertDefaultOutputFormat() throws IOException
    {
        runCliStarter(asList(CONVERT_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile).length(), greaterThan(10000));
    }

    @Test
    public void testConvertDefaultInputDir() throws IOException
    {
        runCliStarter(asList(CONVERT_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile).length(), greaterThan(10000));
    }

    @Test
    public void testConvertToSpecobjectStdOutNoOutputFile() throws IOException
    {
        runCliStarter(asList(CONVERT_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FORMAT_PARAMETER, "specobject"));
        assertThat(Files.exists(this.outputFile), equalTo(false));
    }

    @Test
    public void testTraceNoArg()
    {
        runCliStarter(asList(CONVERT_COMMAND));
        assertThat(Files.exists(this.outputFile), equalTo(false));
    }

    @Test
    public void testTrace() throws IOException
    {
        runCliStarter(asList(TRACE_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile).length(), greaterThan(1500));
    }

    @Test
    public void testTraceWithReportVerbosityMinimal() throws IOException
    {
        runCliStarter(asList(TRACE_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), REPORT_VERBOSITY_PARAMETER,
                "MINIMAL"));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile), equalTo("not ok\n"));
    }

    @Test
    public void testTraceWithReportVerbosityQuiet() throws IOException
    {
        runCliStarter(asList(TRACE_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), REPORT_VERBOSITY_PARAMETER,
                "QUIET"));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile), equalTo(""));
    }

    @Test
    public void testTraceDefaultInputDir() throws IOException
    {
        runCliStarter(asList(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(true));
        assertThat(fileContent(this.outputFile).length(), greaterThan(1500));
    }

    @Test
    public void testTraceStdOutNoOutputFile() throws IOException
    {
        runCliStarter(asList(TRACE_COMMAND, INPUT_DIR_PARAMETER, this.docDir.toString()));
        assertThat(Files.exists(this.outputFile), equalTo(false));
    }

    private String fileContent(final Path file) throws IOException
    {
        return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
    }

    private void expectException(final List<String> args,
            final Class<? extends Exception> expectedExceptionType, final String expectedMessage)
    {
        this.thrown.expect(expectedExceptionType);
        this.thrown.expectMessage(expectedMessage);
        runCliStarter(args);
    }

    private void runCliStarter(final List<String> args)
    {
        CliStarter.main(args.toArray(new String[0]));
    }
}
