package org.itsallcode.openfasttrace.cli;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.core.cli.CliStarter;
import org.itsallcode.openfasttrace.core.cli.ExitStatus;
import org.itsallcode.openfasttrace.core.cli.StandardDirectoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CliStarterInternalIT {
    private static final String SPECOBJECT_PREAMBLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + System.lineSeparator() +"<specdocument>";
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

    private static final Path DOC_DIR = Paths.get("../core/src/test/resources/markdown").toAbsolutePath();

    private Path outputFile;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) {
        this.outputFile = tempDir.resolve("stream.txt");
        this.outContent = new ByteArrayOutputStream();
        this.errContent = new ByteArrayOutputStream();
        this.originalOut = System.out;
        this.originalErr = System.err;
        System.setOut(new PrintStream(this.outContent));
        System.setErr(new PrintStream(this.errContent));
    }

    @AfterEach
    void afterEach() {
        System.setOut(this.originalOut);
        System.setErr(this.originalErr);
    }

    private String getStdOut() {
        return this.outContent.toString(StandardCharsets.UTF_8);
    }

    private String getStdErr() {
        return this.errContent.toString(StandardCharsets.UTF_8);
    }

    @Test
    void testNoArguments() {
        final ExitStatus status = runInternal();
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.CLI_ERROR)),
            () -> assertThat(getStdErr(), startsWith("oft: Missing command"))
        );
    }

    @Test
    // [itest->dsn~cli.command-selection~1]
    void testIllegalCommand() {
        final ExitStatus status = runInternal(ILLEGAL_COMMAND);
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.CLI_ERROR)),
            () -> assertThat(getStdErr(), startsWith("oft: '" + ILLEGAL_COMMAND + "' is not an OFT command."))
        );
    }

    @ValueSource(strings = {HELP_COMMAND, "-h", "--help"})
    @ParameterizedTest
    void testHelpPrintsUsage(final String command) {
        final ExitStatus status = runInternal(command);
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertThat(getStdOut(), startsWith("OpenFastTrace" + System.lineSeparator() +
                    System.lineSeparator() + "Usage:"))
        );
    }

    @Test
    // [itest->dsn~cli.command-selection~1]
    void testConvertWithoutExplicitInputs() {
        final ExitStatus status = runInternal(CONVERT_COMMAND);
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertThat(getStdOut(), startsWith(SPECOBJECT_PREAMBLE))
        );
    }

    @Test
    void testConvertUnknownExporter() {
        final ExitStatus status = runInternal(
                CONVERT_COMMAND, DOC_DIR.toString(),
                OUTPUT_FORMAT_PARAMETER, "illegal",
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.CLI_ERROR)),
            () -> assertThat(getStdErr(), startsWith("oft: export format 'illegal' is not supported."))
        );
    }

    @Test
    // [itest->dsn~cli.conversion.output-format~1]
    void testConvertToSpecobjectFile() {
        final ExitStatus status = runInternal(
                CONVERT_COMMAND, DOC_DIR.toString(),
                OUTPUT_FORMAT_PARAMETER, "specobject",
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                COLOR_SCHEME_PARAMETER, "BLACK_AND_WHITE");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith(SPECOBJECT_PREAMBLE + System.lineSeparator()
                    + "  <specobjects doctype=\"")
        );
    }

    @Test
    // [itest->dsn~cli.conversion.default-output-format~1]
    void testConvertDefaultOutputFormat() {
        final ExitStatus status = runInternal(CONVERT_COMMAND, DOC_DIR.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertThat(getStdOut(), startsWith(SPECOBJECT_PREAMBLE))
        );
    }

    @Test
    // [itest->dsn~cli.input-file-selection~1]
    void testConvertDefaultOutputFormatIntoFile() {
        final ExitStatus status = runInternal(CONVERT_COMMAND, DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith(SPECOBJECT_PREAMBLE)
        );
    }

    @Test
    // [itest->dsn~cli.default-input~1]
    void testConvertDefaultInputDir() {
        final ExitStatus status = runInternal(
                CONVERT_COMMAND,
                OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith(SPECOBJECT_PREAMBLE),
            () -> assertOutputFileLength(2000)
        );
    }

    @Test
    void testTraceNoArguments() {
        final ExitStatus status = runInternalWithWorkingDir(Path.of(".").toAbsolutePath(), TRACE_COMMAND);
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.FAILURE)),
            () -> assertThat(getStdOut(), containsString("not ok\u001B[0m - 43 total, 43 defect"))
        );
    }

    @Test
    // [itest->dsn~cli.command-selection~1]
    void testTrace() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND,
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                DOC_DIR.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith("ok - 5 total")
        );
    }

    @Test
    void testTraceWithReportVerbosityMinimal() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND, DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                REPORT_VERBOSITY_PARAMETER, "MINIMAL");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith("ok")
        );
    }

    @Test
    void testTraceWithReportVerbosityQuietToStdOut() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND, DOC_DIR.toString(),
                REPORT_VERBOSITY_PARAMETER, "QUIET");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertThat(getStdOut(), is(emptyString())),
            () -> assertOutputFileExists(false)
        );
    }

    @Test
    void testTraceWithReportVerbosityQuietToFileMustBeRejected() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND, DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                REPORT_VERBOSITY_PARAMETER, "QUIET");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.CLI_ERROR)),
            () -> assertThat(getStdErr(), containsString("oft: combining stream verbosity 'quiet' and output to file is not supported."))
        );
    }

    @Test
    // [itest->dsn~cli.default-input~1]
    void testTraceDefaultInputDir() {
        final ExitStatus status = runInternal(TRACE_COMMAND, OUTPUT_FILE_PARAMETER, this.outputFile.toString());
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.FAILURE)),
            () -> assertThat(getStdOut(), is(emptyString())),
            () -> assertOutputFileExists(true)
        );
    }

    @Test
    void testBasicHtmlTrace() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND, DOC_DIR.toString(),
                OUTPUT_FORMAT_PARAMETER, "html");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertThat(getStdOut(), startsWith("<!DOCTYPE html>"))
        );
    }

    @Test
    // [itest->dsn~cli.tracing.output-format~1]
    void testTraceOutputFormatPlain() {
        final ExitStatus status = runInternal(TRACE_COMMAND, DOC_DIR.toString(), OUTPUT_FILE_PARAMETER,
                this.outputFile.toString(), OUTPUT_FORMAT_PARAMETER, "plain");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith("ok - 5 total")
        );
    }

    @Test
    void testTraceMacNewlines() {
        final ExitStatus status = runInternal(TRACE_COMMAND,
                        OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                        NEWLINE_PARAMETER, "OLDMAC",
                        DOC_DIR.toString());
        assertAll(
                () -> assertThat(status, equalTo(ExitStatus.OK)),
                () -> assertOutputFileExists(true),
                this::assertOutputFileContainsOldMacNewlines,
                this::assertOutputFileContainsNoUnixNewlines);
    }

    @Test
    // [itest->dsn~cli.default-newline-format~1]
    void testTraceDefaultNewlines() {
        final ExitStatus status = runInternal(TRACE_COMMAND,
                        OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                        DOC_DIR.toString());
        assertAll(
                () -> assertThat(status, equalTo(ExitStatus.OK)),
                () -> assertOutputFileExists(true),
                this::assertPlatformNewlines,
                this::assertNoOffendingNewlines);
    }

    @Test
    void testTraceWithFilteredArtifactType() {
        final ExitStatus status = runInternal(
                TRACE_COMMAND, DOC_DIR.toString(),
                OUTPUT_FILE_PARAMETER, this.outputFile.toString(),
                WANTED_ARTIFACT_TYPES_PARAMETER, "feat,req");
        assertAll(
            () -> assertThat(status, equalTo(ExitStatus.OK)),
            () -> assertOutputFileExists(true),
            () -> assertOutputFileContentStartsWith("ok - 3 total")
        );
    }

    private void assertOutputFileContainsOldMacNewlines() {
        assertThat("Has old Mac newlines", getOutputFileContent().contains(CARRIAGE_RETURN),
                equalTo(true));
    }

    private void assertOutputFileContainsNoUnixNewlines() {
        assertThat("Has no Unix newlines", getOutputFileContent().contains(NEWLINE),
                equalTo(false));
    }

    private void assertPlatformNewlines() {
        assertThat("Has native platform line separator",
                getOutputFileContent().contains(System.lineSeparator()), equalTo(true));
    }

    private void assertNoOffendingNewlines() {
        final String systemLineSeparator = System.lineSeparator();
        switch (systemLineSeparator) {
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
        case CARRIAGE_RETURN + NEWLINE:
            assertThat("Has no carriage return without newline and vice-versa",
                    getOutputFileContent().matches("\r[^\n]|[^\r]\n"), equalTo(false));
            break;
        default:
            final String hexCode = systemLineSeparator.chars()
                    .mapToObj(c -> String.format("\\u%04x", c))
                    .collect(joining());
            fail("Unsupported line separator '%s' (%s)".formatted(systemLineSeparator, hexCode));
        }
    }

    private void assertOutputFileContentStartsWith(final String content) {
        assertThat(getOutputFileContent(), startsWith(content));
    }

    private void assertOutputFileExists(final boolean fileExists) {
        assertThat("Output file %s exists".formatted(this.outputFile), Files.exists(this.outputFile),
                equalTo(fileExists));
    }

    private void assertOutputFileLength(final int length) {
        assertThat(getOutputFileContent().length(), greaterThan(length));
    }

    private String getOutputFileContent() {
        final Path file = this.outputFile;
        if (!Files.exists(file)) {
            throw new AssertionError("Expected output file %s does not exist".formatted(file));
        }
        try {
            return Files.readString(file);
        } catch (final IOException exception) {
            throw new UncheckedIOException("Failed to read file %s".formatted(file), exception);
        }
    }

    private ExitStatus runInternal(final String... arguments) {
        return runInternalWithWorkingDir(Path.of("..").toAbsolutePath(), arguments);
    }

    private ExitStatus runInternalWithWorkingDir(final Path workingDir, final String... arguments) {
        return CliStarter.mainDelegate(arguments, new StandardDirectoryService() {
            @Override
            public String getCurrent() {
                return workingDir.toString();
            }
        });
    }
}
