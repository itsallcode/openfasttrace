package org.itsallcode.openfasttrace.cli;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.itsallcode.junit.sysextensions.AssertExit.assertExitWithStatus;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.*;

import org.itsallcode.io.Capturable;
import org.itsallcode.junit.sysextensions.*;
import org.itsallcode.junit.sysextensions.SystemErrGuard.SysErr;
import org.itsallcode.junit.sysextensions.SystemOutGuard.SysOut;
import org.itsallcode.junit.sysextensions.security.ExitTrapException;
import org.itsallcode.openfasttrace.core.cli.CliStarter;
import org.itsallcode.openfasttrace.core.cli.ExitStatus;
import org.itsallcode.openfasttrace.testutil.cli.FakeDirectoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.MultipleFailuresError;

@ExtendWith(ExitGuard.class)
@ExtendWith(SystemOutGuard.class)
@ExtendWith(SystemErrGuard.class)
// [itest->dsn~cli.tracing.exit-status~1]
class TestCliStarter
{
    private static final String SPECOBJECT_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<specdocument>";
    private static final String ILLEGAL_COMMAND = "illegal";
    private static final String NEWLINE_PARAMETER = "--newline";
    private static final String HELP_COMMAND = "help";
    private static final String CONVERT_COMMAND = "convert";
    private static final String TRACE_COMMAND = "trace";
    private static final String OUTPUT_FILE_PARAMETER = "--output-file";
    private static final String REPORT_VERBOSITY_PARAMETER = "--report-verbosity";
    private static final String OUTPUT_FORMAT_PARAMETER = "--output-format";
    private static final String WANTED_ARTIFACT_TYPES_PARAMETER = "--wanted-artifact-types";
    private static final String COLOR_SCHEME_PARAMETER = "--color-scheme";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String NEWLINE = "\n";

    private final Path DOC_DIR = Paths.get("../core/src/test/resources/markdown").toAbsolutePath();

    private Path outputFile;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir)
    {
        this.outputFile = tempDir.resolve("stream.txt");
    }

    @Test
    void testNoArguments(@SysErr final Capturable err)
    {
        assertExitWithError(this::runCliStarter, ExitStatus.CLI_ERROR, "oft: Missing command",
                err);
    }

    private void assertExitWithError(final Runnable runnable, final ExitStatus status,
            final String message, final Capturable stream) throws MultipleFailuresError
    {
        stream.capture();
        assertAll( //
                () -> assertExitWithStatus(status.getCode(), runnable),
                () -> assertThat(stream.getCapturedData(), startsWith(message)) //
        );
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testIllegalCommand(@SysErr final Capturable err)
    {
        assertExitWithError(() -> runCliStarter(ILLEGAL_COMMAND), ExitStatus.CLI_ERROR,
                "oft: '" + ILLEGAL_COMMAND + "' is not an OFT command.", err);
    }

    @Test
    void testHelpPrintsUsage(@SysOut final Capturable out)
    {
        assertExitOkWithStdOutStart(() -> runCliStarter(HELP_COMMAND), "OpenFastTrace\n\nUsage:", out);
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testConvertWithoutExplicitInputs(@SysOut final Capturable out)
    {
        assertExitOkWithStdOutStart(() -> runCliStarter(CONVERT_COMMAND), SPECOBJECT_PREAMBLE, out);
    }

    private void assertExitOkWithStdOutStart(final Runnable runnable, final String outputStart,
            final Capturable out) throws MultipleFailuresError
    {
        out.capture();
        assertAll(() -> assertExitWithStatus(ExitStatus.OK.getCode(), runnable), () -> assertOutputFileExists(false), //
                () -> assertThat(out.getCapturedData(), startsWith(outputStart)));
    }

    @Test
    void testConvertUnknownExporter(@SysErr final Capturable err)
    {
        final Runnable runnable = () -> runCliStarter( //
                CONVERT_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FORMAT_PARAMETER, "illegal", //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertExitWithError(runnable, ExitStatus.CLI_ERROR,
                "oft: export format 'illegal' is not supported.", err);
    }

    // [itest->dsn~cli.conversion.output-format~1]
    @Test
    void testConvertToSpecobjectFile() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                CONVERT_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FORMAT_PARAMETER, "specobject", //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                COLOR_SCHEME_PARAMETER, "BLACK_AND_WHITE");
        assertExitOkWithOutputFileStart(runnable,
                SPECOBJECT_PREAMBLE + "\n  <specobjects doctype=\"");
    }

    private void assertExitOkWithOutputFileStart(final Runnable runnable, final String fileStart)
            throws MultipleFailuresError
    {
        assertAll( //
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), runnable), //
                () -> assertOutputFileExists(true), //
                () -> assertOutputFileContentStartsWith(fileStart) //
        );
    }

    // [itest->dsn~cli.conversion.default-output-format~1]
    @Test
    void testConvertDefaultOutputFormat(@SysOut final Capturable out) throws IOException
    {
        final Runnable runnable = () -> runCliStarter(CONVERT_COMMAND, this.DOC_DIR.toString());
        assertExitOkWithStdOutStart(runnable, SPECOBJECT_PREAMBLE, out);
    }

    // [itest->dsn~cli.input-file-selection~1]
    @Test
    void testConvertDefaultOutputFormatIntoFile() throws IOException
    {
        final Runnable runnable = () -> runCliStarter(CONVERT_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertExitOkWithOutputFileStart(runnable, SPECOBJECT_PREAMBLE);
    }

    // [itest->dsn~cli.default-input~1]
    @Test
    void testConvertDefaultInputDir() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                CONVERT_COMMAND, //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString() //
        );
        assertExitOkWithOutputFileOfLength(runnable, 2000);
    }

    @Test
    void testTraceNoArguments(@SysErr final Capturable err)
    {
        // This test is fragile, since we can't influence the current working
        // directory which is automatically used if no directory is specified.
        // All we know is that no CLI error should be returned.
        try
        {
            runCliStarter(TRACE_COMMAND);
        }
        catch (final ExitTrapException e)
        {
            assertThat(e.getExitStatus(),
                    anyOf(equalTo(ExitStatus.OK.getCode()), equalTo(ExitStatus.FAILURE.getCode())));
            assertThat(err.getCapturedData(), isEmptyOrNullString());
        }
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testTrace() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                this.DOC_DIR.toString() //
        );
        assertExitOkWithOutputFileStart(runnable, "ok - 5 total");
    }

    @Test
    void testTraceWithReportVerbosityMinimal() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "MINIMAL" //
        );
        assertExitOkWithOutputFileStart(runnable, "ok");
    }

    @Test
    void testTraceWithReportVerbosityQuietToStdOut(@SysOut final Capturable out) throws IOException
    {
        final Runnable runnable = () -> runCliStarter(//
                TRACE_COMMAND, this.DOC_DIR.toString(), //
                REPORT_VERBOSITY_PARAMETER, "QUIET" //
        );
        out.capture();
        assertAll( //
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), runnable), //
                () -> assertOutputFileExists(false),
                () -> assertThat(out.getCapturedData(), isEmptyOrNullString()) //
        );
    }

    @Test
    void testTraceWithReportVerbosityQuietToFileMustBeRejected(@SysErr final Capturable err)
            throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                REPORT_VERBOSITY_PARAMETER, "QUIET" //
        );
        assertExitWithError(runnable, ExitStatus.CLI_ERROR, "oft: combining stream", err);
    }

    @Test
    // [itest->dsn~cli.default-input~1]
    void testTraceDefaultInputDir(@SysErr final Capturable err) throws IOException
    {
        // This test is fragile, since we can't influence the current working
        // directory which is automatically used if no directory is specified.
        // All we know is that no CLI error should be returned and an output
        // file must exist.
        try
        {
            runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        }
        catch (final ExitTrapException e)
        {
            assertAll( //
                    () -> assertThat(e.getExitStatus(),
                            anyOf(equalTo(ExitStatus.OK.getCode()),
                                    equalTo(ExitStatus.FAILURE.getCode()))),
                    () -> assertThat(err.getCapturedData(), isEmptyOrNullString()),
                    () -> assertOutputFileExists(true));
        }
    }

    @Test
    void testBasicHtmlTrace(@SysOut final Capturable out)
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FORMAT_PARAMETER, "html");
        assertExitOkWithStdOutStart(runnable, "<!DOCTYPE html>", out);
    }

    private void assertExitOkWithOutputFileOfLength(final Runnable runnable, final int length)
            throws MultipleFailuresError
    {
        assertAll( //
                () -> assertExitOkWithOutputFileStart(runnable, SPECOBJECT_PREAMBLE), //
                () -> assertOutputFileLength(length) //
        );
    }

    private void assertOutputFileLength(final int length)
    {
        assertThat(getOutputFileContent().length(), greaterThan(length));
    }

    // [itest->dsn~cli.tracing.output-format~1]]
    void testTraceOutputFormatPlain() throws IOException
    {
        final Runnable runnable = () -> runCliStarter(TRACE_COMMAND, OUTPUT_FILE_PARAMETER,
                this.outputFile.toString(), OUTPUT_FORMAT_PARAMETER, "plain");
        assertExitOkWithOutputFileOfLength(runnable, 1000);
    }

    @Test
    void testTraceMacNewlines() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                NEWLINE_PARAMETER, "OLDMAC", //
                this.DOC_DIR.toString() //
        );
        assertAll( //
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), runnable), //
                () -> assertOutputFileExists(true), //
                this::assertOutputFileContainsOldMacNewlines, //
                this::assertOutputFileContainsNoUnixNewlines //
        );
    }

    private void assertOutputFileContainsOldMacNewlines()
    {
        assertThat("Has old Mac newlines", getOutputFileContent().contains(CARRIAGE_RETURN),
                equalTo(true));
    }

    private void assertOutputFileContainsNoUnixNewlines()
    {
        assertThat("Has no Unix newlines", getOutputFileContent().contains(NEWLINE),
                equalTo(false));
    }

    @Test
    // [itest->dsn~cli.default-newline-format~1]
    void testTraceDefaultNewlines() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                this.DOC_DIR.toString() //
        );
        assertAll( //
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), runnable), //
                () -> assertOutputFileExists(true), //
                this::assertPlatformNewlines, //
                this::assertNoOffendingNewlines //
        );
    }

    private void assertPlatformNewlines()
    {
        assertThat("Has native platform line separator",
                getOutputFileContent().contains(System.lineSeparator()), equalTo(true));
    }

    private void assertNoOffendingNewlines()
    {
        switch (System.lineSeparator())
        {
        case NEWLINE:
            assertThat("Has no carriage returns", getOutputFileContent().contains(CARRIAGE_RETURN),
                    equalTo(false));
            break;
        case CARRIAGE_RETURN:
            assertThat("Has no newlines", getOutputFileContent().contains(NEWLINE), equalTo(false));
            break;
        case NEWLINE + CARRIAGE_RETURN:
            assertThat("Has no newline without carriage return and vice-versa",
                    getOutputFileContent().matches("\n[^\r]|[^\n]\r"), equalTo(false));
            break;
        }
    }

    @Test
    void testTraceWithFilteredArtifactType() throws IOException
    {
        final Runnable runnable = () -> runCliStarter( //
                TRACE_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                WANTED_ARTIFACT_TYPES_PARAMETER, "feat,req" //
        );
        assertExitOkWithOutputFileStart(runnable, "ok - 3 total");
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
            return Files.readString(file);
        }
        catch (final IOException exception)
        {
            // Need to convert this to an unchecked exception. Otherwise, we get
            // stuck with the checked exceptions in the assertion lambdas.
            throw new RuntimeException(exception);
        }
    }

    private void runCliStarter(final String... arguments)
    {
        CliStarter.main(arguments, new FakeDirectoryService(this.DOC_DIR.toString()));
    }
}