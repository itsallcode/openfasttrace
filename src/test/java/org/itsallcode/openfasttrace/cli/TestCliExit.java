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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.itsallcode.openfasttrace.cli.CliArguments;
import org.itsallcode.openfasttrace.cli.CliStarter;
import org.itsallcode.openfasttrace.cli.ExitStatus;
import org.itsallcode.openfasttrace.cli.commands.TraceCommand;
import org.junit.Rule;


import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class TestCliExit
{
    private static final String TEST_RESOURCES_MARKDOWN = "src/test/resources/markdown";
    private static final String SAMPLE_DESIGN = TEST_RESOURCES_MARKDOWN + "/sample_design.md";
    private static final String SAMPLE_SYSTEM_REQUIREMENTS = TEST_RESOURCES_MARKDOWN
            + "/sample_system_requirements.md";
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void testCliExitCode_Ok()
    {
        assertExitStatusForTracedFiles(ExitStatus.OK, SAMPLE_SYSTEM_REQUIREMENTS, SAMPLE_DESIGN);
    }

    private void assertExitStatusForTracedFiles(final ExitStatus expectedStatus,
            final String... files)
    {
        assertExitStatusForCommandWithFiles(expectedStatus, TraceCommand.COMMAND_NAME, files);
    }

    private void assertExitStatusForCommandWithFiles(final ExitStatus expectedStatus,
            final String command, final String... files)
    {
        final CliArguments arguments = new CliArguments();
        final List<String> values = new ArrayList<>();
        values.add(command);
        values.addAll(Arrays.asList(files));
        arguments.setUnnamedValues(values);
        this.exit.expectSystemExitWithStatus(expectedStatus.getCode());
        new CliStarter(arguments).run();
    }

    @Test
    public void testCliExitCode_Failure()
    {
        assertExitStatusForTracedFiles(ExitStatus.FAILURE, SAMPLE_SYSTEM_REQUIREMENTS);
    }
}
