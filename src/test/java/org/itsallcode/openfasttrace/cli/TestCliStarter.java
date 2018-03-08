package org.itsallcode.openfasttrace.cli;

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
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;

public class TestCliStarter
{
    private static final String NEWLINE = "\n";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String REQM2_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><specdocument>";
    private static final String ILLEGAL_COMMAND = "illegal";
    private static final String NEWLINE_PARAMETER = "--newline";
    private static final String CONVERT_COMMAND = "convert";
    private static final String TRACE_COMMAND = "trace";
    private static final String OUTPUT_FILE_PARAMETER = "--output-file";
    private static final String REPORT_VERBOSITY_PARAMETER = "--report-verbosity";
    private static final String OUTPUT_FORMAT_PARAMETER = "--output-format";
    private static final String WANTED_ARTIFACT_TYPES_PARAMETER = "--wanted-artifact-types";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Path docDir;
    private Path outputFile;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();

    @Before
    public void setUp() throws UnsupportedEncodingException
    {
        this.docDir = Paths.get("src", "test", "resources", "markdown").toAbsolutePath();
        this.outputFile = this.tempFolder.getRoot().toPath().resolve("report.txt");
        System.setOut(new PrintStream(this.outputStream, true, "UTF-8"));
        System.setErr(new PrintStream(this.error, true, "UTF-8"));
    }

    @Test
    public void testNoArguments()
    {
        expectCliExitOnErrorThatStartsWith(ExitStatus.CLI_ERROR, "oft: Missing command");
        runCliStarter();

    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    public void testIllegalCommand()
    {

        expectCliExitOnErrorThatStartsWith(ExitStatus.CLI_ERROR,
                "oft: '" + ILLEGAL_COMMAND + "' is not an OFT command.");
        runCliStarter(ILLEGAL_COMMAND);
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    public void testConvertWithoutExplicitInputs()
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(false);
            assertStdOutStartsWith(REQM2_PREAMBLE);
        });
        runCliStarter(CONVERT_COMMAND);
    }

    @Test
    public void testConvertUnknownExporter()
    {
        expectCliExitOnErrorThatStartsWith(ExitStatus.CLI_ERROR,
                "oft: export format 'illegal' is not supported.");
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), OUTPUT_FORMAT_PARAMETER, "illegal",
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    // [itest->dsn~cli.conversion.output-format~1]
    @Test
    public void testConvertToSpecobjectFile() throws IOException
    {
        expectStandardFileExportResult();
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), //
                OUTPUT_FORMAT_PARAMETER, "specobject", //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    // [itest->dsn~cli.conversion.default-output-format~1]
    @Test
    public void testConvertDefaultOutputFormat() throws IOException
    {
        expectCliExitStatusWithAssertions(ExitStatus.OK, () -> {
            assertOutputFileExists(false);
            assertStdOutStartsWith(REQM2_PREAMBLE);
        });
        runCliStarter(CONVERT_COMMAND, this.docDir.toString());
    }

    // [itest->dsn~cli.input-file-selection~1]
    @Test
    public void testConvertDefaultOutputFormatIntoFile() throws IOException
    {
        expectStandardFileExportResult();
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), OUTPUT_FILE_PARAMETER,
                this.outputFile.toString());
    }

    // [itest->dsn~cli.default-input~1]
    @Test
    public void testConvertDefaultInputDir() throws IOException
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertThat(getOutputFileContent().length(), greaterThan(10000));
        });
        runCliStarter(CONVERT_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    @Test
    public void testTraceNoArguments()
    {
        expectCliExitWithAssertions(() -> {
            assertOutputFileExists(false);
        });
        runCliStarter(TRACE_COMMAND);
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    public void testTrace() throws IOException
    {
        expectStandardReportFileResult();
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    @Test
    public void testTraceWithReportVerbosityMinimal() throws IOException
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertOutputFileContentStartsWith("ok");
        });
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "MINIMAL");
    }

    @Test
    public void testTraceWithReportVerbosityQuietToStdOut() throws IOException
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(false);
            assertStdOutEmpty();
        });
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                REPORT_VERBOSITY_PARAMETER, "QUIET");
    }

    @Test
    public void testTraceWithReportVerbosityQuietToFileMustBeRejected() throws IOException
    {
        expectCliExitOnErrorThatStartsWith(ExitStatus.CLI_ERROR, "oft: combining report");
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "QUIET");
    }

    @Test
    // [itest->dsn~cli.default-input~1]
    public void testTraceDefaultInputDir() throws IOException
    {
        expectCliExitWithAssertions(() -> {
            assertOutputFileExists(true);
            assertThat(getOutputFileContent().length(), greaterThan(400));
        });
        runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    private void expectStandardFileExportResult()
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertOutputFileContentStartsWith(REQM2_PREAMBLE + "<specobjects doctype=\"");
        });
    }

    // [itest->dsn~cli.tracing.output-format~1]]
    public void testTraceOutputFormatPlain() throws IOException
    {
        expectCliExitWithAssertions(() -> {
            assertOutputFileExists(true);
            assertThat(getOutputFileContent().length(), greaterThan(1000));
        });
        runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                OUTPUT_FORMAT_PARAMETER, "plain");
    }

    @Test
    public void testTraceMacNewlines() throws IOException
    {
        expectCliExitOkWithAssertions(() -> {
            assertThat(Files.exists(this.outputFile), equalTo(true));
            assertThat("Has old Mac newlines", getOutputFileContent().contains(CARRIAGE_RETURN),
                    equalTo(true));
            assertThat("Has no Unix newlines", getOutputFileContent().contains(NEWLINE),
                    equalTo(false));
        });
        runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                this.docDir.toString(), NEWLINE_PARAMETER, "OLDMAC");
    }

    @Test
    // [itest->dsn~cli.default-newline-format~1]
    public void testTraceDefaultNewlines() throws IOException
    {
        expectCliExitOkWithAssertions(() -> {
            assertThat(Files.exists(this.outputFile), equalTo(true));
            assertThat("Has native platform line separator",
                    getOutputFileContent().contains(System.lineSeparator()), equalTo(true));
            switch (System.lineSeparator())
            {
            case NEWLINE:
                assertThat("Has no carriage returns",
                        getOutputFileContent().contains(CARRIAGE_RETURN), equalTo(false));
                break;

            case CARRIAGE_RETURN:
                assertThat("Has no newlines", getOutputFileContent().contains(NEWLINE),
                        equalTo(false));
                break;

            case NEWLINE + CARRIAGE_RETURN:
                assertThat("Has no newline without carriage return and vice-versa",
                        getOutputFileContent().matches("\n[^\r]|[^\n]\r"), equalTo(false));
                break;
            }
        });
        runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                this.docDir.toString());
    }

    @Test
    public void testTraceWithFilteredArtifactType() throws IOException
    {
        expectReducedReportFileResult();
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), WANTED_ARTIFACT_TYPES_PARAMETER,
                "feat,req");
    }

    private void expectReducedReportFileResult()
    {
        final int expectedNumber = 3;
        expectCliExitOkWithNumberOfItems(expectedNumber);
    }

    private void expectStandardReportFileResult()
    {
        final int expectedNumber = 5;
        expectCliExitOkWithNumberOfItems(expectedNumber);
    }

    private void expectCliExitOkWithNumberOfItems(final int expectedNumber)
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertOutputFileContentStartsWith("ok - " + expectedNumber + " total");
        });
    }

    private void expectCliExitOkWithAssertions(final ExitAssertable assertions)
    {
        expectCliExitStatusWithAssertions(ExitStatus.OK, assertions);
    }

    // [itest->dsn~cli.tracing.exit-status~1]
    private void expectCliExitStatusWithAssertions(final ExitStatus status,
            final ExitAssertable assertions)
    {
        this.exit.expectSystemExitWithStatus(status.getCode());
        this.exit.checkAssertionAfterwards(new Assertion()
        {
            @Override
            public void checkAssertion() throws Exception
            {
                assertions.doAsserts();
            }
        });
    }

    private void expectCliExitWithAssertions(final ExitAssertable assertions)
    {
        this.exit.expectSystemExit();
        this.exit.checkAssertionAfterwards(new Assertion()
        {
            @Override
            public void checkAssertion() throws Exception
            {
                assertions.doAsserts();
            }
        });
    }

    private void expectCliExitOnErrorThatStartsWith(final ExitStatus status,
            final String expectedError)
    {
        this.exit.expectSystemExitWithStatus(status.getCode());
        this.exit.checkAssertionAfterwards(new Assertion()
        {
            @Override
            public void checkAssertion() throws Exception
            {
                assertThat(TestCliStarter.this.error.toString(), startsWith(expectedError));
            }
        });
    }

    private void assertStdOutStartsWith(final String content)
    {
        assertThat(TestCliStarter.this.outputStream.toString(), startsWith(content));
    }

    private void assertStdOutEmpty()
    {
        assertThat("STDOUT stream is empty", TestCliStarter.this.outputStream.toString(),
                equalTo(""));
    }

    private void assertOutputFileContentStartsWith(final String content)
    {
        assertThat(getOutputFileContent(), startsWith(content));
    }

    private void assertOutputFileExists(final boolean fileExists)
    {
        assertThat("Output file exists", Files.exists(this.outputFile), equalTo(fileExists));
    }

    private String getOutputFileContent()
    {
        final Path file = this.outputFile;
        try
        {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        }
        catch (final IOException exception)
        {
            // Need to convert this to an unchecked exception. Otherwise we get
            // stuck with the checked exceptions in the assertion lambdas.
            throw new RuntimeException(exception);
        }
    }

    private void runCliStarter(final String... arguments)
    {
        CliStarter.main(arguments);
    }

    interface ExitAssertable
    {
        void doAsserts();
    }
}
