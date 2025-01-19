package org.itsallcode.openfasttrace.core.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.junit.sysextensions.AssertExit;
import org.itsallcode.junit.sysextensions.ExitGuard;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.itsallcode.openfasttrace.testutil.TestAssumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@SuppressWarnings("removal") // https://github.com/itsallcode/openfasttrace/issues/436
@ExtendWith(ExitGuard.class)
class TestCliStarter
{
    @BeforeAll
    static void assumeSecurityManagerSupported()
    {
        TestAssumptions.assumeSecurityManagerSupported();
    }

    @Test
    void testRunWithoutArguments()
    {
        AssertExit.assertExitWithStatus(2, () -> run());
    }

    @Test
    void testRunThrowingException()
    {
        final ExporterException exception = assertThrows(ExporterException.class,
                () -> run("trace"));
        assertThat(exception.getMessage(),
                equalTo("Found no matching reporter for output format 'plain'"));
    }

    @Test
    void testRunWithUnknownCommand()
    {
        AssertExit.assertExitWithStatus(2, () -> run("unknownCommand"));
    }

    @Test
    void testRunWithHelpCommand()
    {
        AssertExit.assertExitWithStatus(0, () -> run("help"));
    }

    private void run(final String... args)
    {
        CliStarter.main(args);
    }
}
