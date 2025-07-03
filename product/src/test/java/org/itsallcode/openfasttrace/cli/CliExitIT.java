package org.itsallcode.openfasttrace.cli;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

import org.itsallcode.openfasttrace.core.cli.ExitStatus;
import org.itsallcode.openfasttrace.core.cli.commands.TraceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CliExitIT
{
    private static final String TEST_RESOURCES_MARKDOWN = "../core/src/test/resources/markdown";
    private static final String SAMPLE_DESIGN = TEST_RESOURCES_MARKDOWN + "/sample_design.md";
    private static final String SAMPLE_SYSTEM_REQUIREMENTS = TEST_RESOURCES_MARKDOWN
            + "/sample_system_requirements.md";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Test
    void testRunWithoutArguments()
    {
        jarLauncher()
                .args(emptyList())
                .expectedExitCode(ExitStatus.CLI_ERROR.getCode())
                .expectStdOut(emptyString())
                .expectStdErr(equalTo("oft: Missing command\nAdd one of 'help','convert','trace'\n\n"))
                .start()
                .waitUntilTerminated(TIMEOUT);
    }

    @Test
    void testRunWithUnsupportedCommand()
    {
        jarLauncher()
                .args(List.of("unsupported"))
                .expectedExitCode(ExitStatus.CLI_ERROR.getCode())
                .expectStdOut(emptyString())
                .expectStdErr(equalTo(
                        "oft: 'unsupported' is not an OFT command.\nChoose one of 'help','convert','trace'.\n\n"))
                .start()
                .waitUntilTerminated(TIMEOUT);
    }

    @Test
    void testRunWithHelpCommand()
    {
        jarLauncher()
                .args(List.of("help"))
                .expectedExitCode(ExitStatus.OK.getCode())
                .expectStdOut(startsWith("""
                        OpenFastTrace

                        Usage:
                          oft command"""))
                .expectStdErr(emptyString())
                .start()
                .waitUntilTerminated(TIMEOUT);
    }

    @Test
    void testRunWithUnsupportedReporter(@TempDir final Path emptyDir)
    {
        jarLauncher()
                .args(List.of("trace", "-o", "unknown", emptyDir.toString()))
                .expectedExitCode(ExitStatus.FAILURE.getCode())
                .expectStdOut(emptyString())
                .expectStdErr(startsWith(
                        "Exception in thread \"main\" org.itsallcode.openfasttrace.api.exporter.ExporterException: Found no matching reporter for output format 'unknown'"))
                .start()
                .waitUntilTerminated(TIMEOUT);
    }

    @Test
    void testCliExitCode_Ok()
    {
        assertExitStatusForTracedFiles(ExitStatus.OK, SAMPLE_SYSTEM_REQUIREMENTS, SAMPLE_DESIGN);
    }

    private void assertExitStatusForTracedFiles(final ExitStatus expectedStatus, final String... files)
    {
        final List<String> args = new ArrayList<>();
        args.add(TraceCommand.COMMAND_NAME);
        args.addAll(Arrays.asList(files));
        jarLauncher()
                .args(args)
                .expectedExitCode(expectedStatus.getCode())
                .expectStdErr(emptyString())
                .expectStdOut(not(emptyString()))
                .start()
                .waitUntilTerminated(TIMEOUT);
    }

    private JarLauncher.Builder jarLauncher()
    {
        return JarLauncher.builder()
                .jarNameTemplate("openfasttrace-%s.jar")
                .currentWorkingDir();
    }

    @Test
    void testCliExitCode_Failure()
    {
        assertExitStatusForTracedFiles(ExitStatus.FAILURE, SAMPLE_SYSTEM_REQUIREMENTS);
    }

    @Test
    void testCliExitCode_CliError()
    {
        jarLauncher()
                .args(List.of("--zzzz"))
                .expectedExitCode(ExitStatus.CLI_ERROR.getCode())
                .expectStdOut(emptyString())
                .expectStdErr(equalTo("oft: Unexpected parameter '--zzzz' is not allowed\n"))
                .start()
                .waitUntilTerminated(TIMEOUT);
    }
}
