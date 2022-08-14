package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.itsallcode.junit.sysextensions.AssertExit;
import org.itsallcode.junit.sysextensions.ExitGuard;
import org.itsallcode.openfasttrace.api.exporter.ExporterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ExitGuard.class)
class TestCliStarter
{
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

    private void run(String... args)
    {
        CliStarter.main(args);
    }
}
