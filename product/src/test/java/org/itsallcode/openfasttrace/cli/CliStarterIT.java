package org.itsallcode.openfasttrace.cli;

import static org.hamcrest.Matchers.*;

import java.nio.file.*;
import java.util.List;

import org.itsallcode.openfasttrace.cli.JarLauncher.Builder;
import org.itsallcode.openfasttrace.core.cli.ExitStatus;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

/**
 * This integration test was reduced to a minimal smoke test.
 * <p>
 * The actual tests are in {@link CliStarterInternalIT}, which makes recording
 * code coverage easier.
 * </p>
 * 
 * @see CliStarterInternalIT
 */
// [itest->dsn~cli.tracing.exit-status~1]
class CliStarterIT
{
    private static final String HELP_COMMAND = "help";

    @Test
    void testNoArguments()
    {
        assertExitWithError(jarLauncher(), ExitStatus.CLI_ERROR, "oft: Missing command");
    }

    private void assertExitWithError(final JarLauncher.Builder jarLauncherBuilder, final ExitStatus status,
            final String message) throws MultipleFailuresError
    {
        jarLauncherBuilder
                .expectStdErr(startsWith(message))
                .expectedExitCode(status.getCode())
                .verify();
    }

    @Test
    void testHelpPrintsUsage()
    {
        jarLauncher(HELP_COMMAND)
                .expectStdOut(startsWith("OpenFastTrace"))
                .expectedExitCode(0)
                .verify();
    }

    private Builder jarLauncher(final String... arguments)
    {
        return jarLauncher().workingDir(Path.of("..").toAbsolutePath()).args(List.of(arguments));
    }

    private JarLauncher.Builder jarLauncher()
    {
        return JarLauncher.builder()
                .currentWorkingDir();
    }
}
