package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.itsallcode.junit.sysextensions.SystemErrGuard;
import org.itsallcode.junit.sysextensions.SystemOutGuard;
import org.itsallcode.openfasttrace.cli.JarLauncher.Builder;
import org.itsallcode.openfasttrace.core.cli.ExitStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.opentest4j.MultipleFailuresError;

@ExtendWith(SystemOutGuard.class)
@ExtendWith(SystemErrGuard.class)
// [itest->dsn~cli.tracing.exit-status~1]
class CliStarterIT
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
    void testNoArguments()
    {
        assertExitWithError(jarLauncher(), ExitStatus.CLI_ERROR, "oft: Missing command");
    }

    private void assertExitWithError(final JarLauncher.Builder jarLauncherBuilder, final ExitStatus status,
            final String message) throws MultipleFailuresError
    {
        jarLauncherBuilder.expectStdOut(startsWith(message)).expectedExitCode(status.getCode()).start();
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testIllegalCommand()
    {
        assertExitWithError(jarLauncher(ILLEGAL_COMMAND), ExitStatus.CLI_ERROR,
                "oft: '" + ILLEGAL_COMMAND + "' is not an OFT command.");
    }

    @Test
    void testHelpPrintsUsage()
    {
        final String nl = System.lineSeparator();
        assertExitOkWithStdOutStart(jarLauncher(HELP_COMMAND), "OpenFastTrace" + nl + nl + "Usage:");
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testConvertWithoutExplicitInputs()
    {
        assertExitOkWithStdOutStart(jarLauncher(CONVERT_COMMAND), SPECOBJECT_PREAMBLE);
    }

    private void assertExitOkWithStdOutStart(final JarLauncher.Builder jarLauncherBuilder, final String outputStart)
            throws MultipleFailuresError
    {
        jarLauncherBuilder.expectStdOut(startsWith(outputStart)).expectedExitCode(0).start();
        assertOutputFileExists(false);
    }

    @Test
    void testConvertUnknownExporter()
    {
        final Builder jarLauncherBuilder = jarLauncher(
                CONVERT_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FORMAT_PARAMETER, "illegal",
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertExitWithError(jarLauncherBuilder, ExitStatus.CLI_ERROR,
                "oft: export format 'illegal' is not supported.");
    }

    // [itest->dsn~cli.conversion.output-format~1]
    @Test
    void testConvertToSpecobjectFile()
    {
        final Builder jarLauncherBuilder = jarLauncher( //
                CONVERT_COMMAND, this.DOC_DIR.toString(), //
                OUTPUT_FORMAT_PARAMETER, "specobject", //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(), //
                COLOR_SCHEME_PARAMETER, "BLACK_AND_WHITE");
        assertExitOkWithOutputFileStart(jarLauncherBuilder,
                SPECOBJECT_PREAMBLE + "\n  <specobjects doctype=\"");
    }

    private void assertExitOkWithOutputFileStart(final JarLauncher.Builder jarLauncherBuilder, final String fileStart)
            throws MultipleFailuresError
    {
        jarLauncherBuilder.expectedExitCode(ExitStatus.OK.getCode()).start();
        assertAll(
                () -> assertOutputFileExists(true),
                () -> assertOutputFileContentStartsWith(fileStart));
    }

    // [itest->dsn~cli.conversion.default-output-format~1]
    @Test
    void testConvertDefaultOutputFormat() throws IOException
    {
        assertExitOkWithStdOutStart(jarLauncher(CONVERT_COMMAND, this.DOC_DIR.toString()), SPECOBJECT_PREAMBLE);
    }

    // [itest->dsn~cli.input-file-selection~1]
    @Test
    void testConvertDefaultOutputFormatIntoFile()
    {
        assertExitOkWithOutputFileStart(jarLauncher(CONVERT_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString()), SPECOBJECT_PREAMBLE);
    }

    // [itest->dsn~cli.default-input~1]
    @Test
    void testConvertDefaultInputDir()
    {
        assertExitOkWithOutputFileOfLength(jarLauncher( //
                CONVERT_COMMAND, //
                OUTPUT_FILE_PARAMETER, this.outputFile.toString() //
        ), 2000);
    }

    @Test
    void testTraceNoArguments()
    {
        // This test is fragile, since we can't influence the current working
        // directory which is automatically used if no directory is specified.
        // All we know is that no CLI error should be returned.
        jarLauncher(TRACE_COMMAND).start();
    }

    // [itest->dsn~cli.command-selection~1]
    @Test
    void testTrace()
    {
        assertExitOkWithOutputFileStart(jarLauncher(
                TRACE_COMMAND,
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                this.DOC_DIR.toString()), "ok - 5 total");
    }

    @Test
    void testTraceWithReportVerbosityMinimal()
    {
        assertExitOkWithOutputFileStart(jarLauncher(
                TRACE_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                REPORT_VERBOSITY_PARAMETER, "MINIMAL"), "ok");
    }

    @Test
    void testTraceWithReportVerbosityQuietToStdOut()
    {
        jarLauncher(
                TRACE_COMMAND, this.DOC_DIR.toString(),
                REPORT_VERBOSITY_PARAMETER, "QUIET").expectStdOut(emptyString())
                .expectedExitCode(ExitStatus.OK.getCode()).start();
        assertOutputFileExists(false);
    }

    @Test
    void testTraceWithReportVerbosityQuietToFileMustBeRejected()
    {
        jarLauncher(
                TRACE_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                REPORT_VERBOSITY_PARAMETER, "QUIET").expectedExitCode(ExitStatus.CLI_ERROR.getCode())
                .expectStdErr(equalTo("oft: combining stream"));
    }

    @Test
    // [itest->dsn~cli.default-input~1]
    void testTraceDefaultInputDir()
    {
        // This test is fragile, since we can't influence the current working
        // directory which is automatically used if no directory is specified.
        // All we know is that no CLI error should be returned and an output
        // file must exist.
        jarLauncher(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString()).expectStdErr(emptyString());
        assertOutputFileExists(true);

    }

    @Test
    void testBasicHtmlTrace()
    {
        assertExitOkWithStdOutStart(jarLauncher(
                TRACE_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FORMAT_PARAMETER, "html"), "<!DOCTYPE html>");
    }

    private void assertExitOkWithOutputFileOfLength(final JarLauncher.Builder jarLauncherBuilder, final int length)
    {
        assertAll(
                () -> assertExitOkWithOutputFileStart(jarLauncherBuilder, SPECOBJECT_PREAMBLE),
                () -> assertOutputFileLength(length));
    }

    private void assertOutputFileLength(final int length)
    {
        assertThat(getOutputFileContent().length(), greaterThan(length));
    }

    // [itest->dsn~cli.tracing.output-format~1]]
    void testTraceOutputFormatPlain()
    {
        assertExitOkWithOutputFileOfLength(jarLauncher(TRACE_COMMAND, OUTPUT_FILE_PARAMETER,
                this.outputFile.toString(), OUTPUT_FORMAT_PARAMETER, "plain"), 1000);
    }

    @Test
    void testTraceMacNewlines()
    {
        assertAll( //
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), TRACE_COMMAND,
                        OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                        NEWLINE_PARAMETER, "OLDMAC",
                        this.DOC_DIR.toString()),
                () -> assertOutputFileExists(true),
                this::assertOutputFileContainsOldMacNewlines,
                this::assertOutputFileContainsNoUnixNewlines);
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
    void testTraceDefaultNewlines()
    {
        assertAll(
                () -> assertExitWithStatus(ExitStatus.OK.getCode(), TRACE_COMMAND,
                        OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                        this.DOC_DIR.toString()),
                () -> assertOutputFileExists(true),
                this::assertPlatformNewlines,
                this::assertNoOffendingNewlines);
    }

    private void assertExitWithStatus(final int code, final String... args)
    {
        jarLauncher().args(List.of(args)).expectedExitCode(code).start();
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
    void testTraceWithFilteredArtifactType()
    {
        assertExitOkWithOutputFileStart(jarLauncher(
                TRACE_COMMAND, this.DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                WANTED_ARTIFACT_TYPES_PARAMETER, "feat,req"), "ok - 3 total");
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

    private Builder jarLauncher(final String... arguments)
    {
        return jarLauncher().args(List.of(arguments));
    }

    private JarLauncher.Builder jarLauncher()
    {
        return JarLauncher.builder()
                .jarNameTemplate("openfasttrace-%s.jar")
                .currentWorkingDir();
    }
}
