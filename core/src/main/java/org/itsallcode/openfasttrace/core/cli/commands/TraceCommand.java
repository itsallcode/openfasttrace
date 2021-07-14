
package org.itsallcode.openfasttrace.core.cli.commands;

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

import java.nio.file.Path;
import java.util.List;

import org.itsallcode.openfasttrace.api.ReportSettings;
import org.itsallcode.openfasttrace.api.core.LinkedSpecificationItem;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.core.Trace;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.core.cli.CliArguments;

/**
 * Handler for requirement tracing CLI command.
 */
public class TraceCommand extends AbstractCommand
{
    /** The command line action for running this command. */
    public static final String COMMAND_NAME = "trace";

    /**
     * Create a {@link TraceCommand}.
     * 
     * @param arguments
     *            command line arguments.
     */
    public TraceCommand(final CliArguments arguments)
    {
        super(arguments);
    }

    @Override
    public boolean run()
    {
        final List<SpecificationItem> items = importItems();
        final List<LinkedSpecificationItem> linkedItems = linkItems(items);
        final Trace trace = traceItems(linkedItems);
        report(this.oft, trace);
        return trace.hasNoDefects();
    }

    private List<LinkedSpecificationItem> linkItems(final List<SpecificationItem> items)
    {
        return this.oft.link(items);
    }

    private Trace traceItems(final List<LinkedSpecificationItem> linkedItems)
    {
        return this.oft.trace(linkedItems);
    }

    private void report(final Oft oft, final Trace trace)
    {
        final Path outputPath = this.arguments.getOutputPath();
        final ReportSettings reportSettings = convertCommandLineArgumentsToReportSettings();
        if (null == outputPath)
        {
            oft.reportToStdOut(trace, reportSettings);
        }
        else
        {
            oft.reportToPath(trace, this.arguments.getOutputPath(), reportSettings);
        }
    }

    private ReportSettings convertCommandLineArgumentsToReportSettings()
    {
        return ReportSettings.builder() //
                .outputFormat(this.arguments.getOutputFormat()) //
                .verbosity(this.arguments.getReportVerbosity()) //
                .newline(this.arguments.getNewline()) //
                .showOrigin(this.arguments.getShowOrigin()) //
                .build();
    }
}