package openfasttrack.cli;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

/*
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

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
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
    private static final String REQM2_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><specdocument>" //
    ;
    private static final String ILLEGAL_COMMAND = "illegal";
    private static final String CONVERT_COMMAND = "convert";
    private static final String TRACE_COMMAND = "trace";
    private static final String OUTPUT_FILE_PARAMETER = "--output-file";
    private static final String REPORT_VERBOSITY_PARAMETER = "--report-verbosity";
    private static final String OUTPUT_FORMAT_PARAMETER = "--output-format";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Path docDir;
    private Path outputFile;

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final ByteArrayOutputStream error = new ByteArrayOutputStream();

    @Before
    public void setUp() throws UnsupportedEncodingException
    {
        this.docDir = Paths.get("src", "test", "resources", "markdown").toAbsolutePath();
        this.outputFile = this.tempFolder.getRoot().toPath().resolve("report.txt");
        System.setOut(new PrintStream(this.output, true, "UTF-8"));
        System.setErr(new PrintStream(this.error, true, "UTF-8"));
    }

    @Test
    public void testNoArguments()
    {
        expectCliExitWithError(ExitStatus.CLI_ERROR, "oft: Missing command");
        runCliStarter();

    }

    @Test
    public void testIllegalCommand()
    {

        expectCliExitWithError(ExitStatus.CLI_ERROR,
                "oft: '" + ILLEGAL_COMMAND + "' is not an OFT command.");
        runCliStarter(ILLEGAL_COMMAND);
    }

    @Test
    public void testConvertWithoutExplicitInputs()
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(false);
            assertStdOutContent(REQM2_PREAMBLE);
        });
        runCliStarter(CONVERT_COMMAND);
    }

    @Test
    public void testConvertUnknownExporter()
    {
        expectCliExitWithError(ExitStatus.CLI_ERROR,
                "oft: export format 'illegal' is not supported.");
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), OUTPUT_FORMAT_PARAMETER, "illegal",
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    @Test
    public void testConvertToSpecobjectFile() throws IOException
    {
        expectStandardFileExportResult();
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), //
                OUTPUT_FORMAT_PARAMETER, "specobject", //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    @Test
    public void testConvertDefaultOutputFormat() throws IOException
    {
        expectStandardFileExportResult();
        runCliStarter(CONVERT_COMMAND, this.docDir.toString(), OUTPUT_FILE_PARAMETER,
                this.outputFile.toString());
    }

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
            assertOutputFileContent("ok");
        });
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "MINIMAL");
    }

    @Test
    public void testTraceWithReportVerbosityQuiet() throws IOException
    {
        expectCliExitWithError(ExitStatus.CLI_ERROR, "oft: combining report");
        runCliStarter(TRACE_COMMAND, this.docDir.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "QUIET");
    }

    @Test
    public void testTraceDefaultInputDir() throws IOException
    {
        expectCliExitWithAssertions(() -> {
            assertOutputFileExists(true);
            assertThat(getOutputFileContent().length(), greaterThan(1000));
        });
        runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString());
    }

    private void expectStandardFileExportResult()
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertOutputFileContent(REQM2_PREAMBLE + "<specobjects doctype=\"dsn\">");
        });
    }

    private void expectStandardReportFileResult()
    {
        expectCliExitOkWithAssertions(() -> {
            assertOutputFileExists(true);
            assertOutputFileContent("ok - 5 total");
        });
    }

    private void expectCliExitOkWithAssertions(final ExitAssertable assertions)
    {
        expectCliExitStatusWithAssertions(ExitStatus.OK, assertions);
    }

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

    private void expectCliExitWithError(final ExitStatus status, final String expectedError)
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

    private void assertStdOutContent(final String content)
    {
        assertThat(TestCliStarter.this.output.toString(), startsWith(content));
    }

    private void assertOutputFileContent(final String content)
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
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return null;
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