package openfasttrack.cli.commands;

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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Linker;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.Trace;
import openfasttrack.core.Tracer;
import openfasttrack.importer.ImporterService;
import openfasttrack.report.ReportService;
import openfasttrack.report.ReportVerbosity;

/**
 * Handler for requirement tracing CLI command.
 */
public class TraceCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "trace";

    final ReportService reportService;
    final Tracer tracer;

    /**
     * Create a {@link TraceCommand}.
     * 
     * @param arguments
     *            the command line arguments.
     */
    public TraceCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new Tracer(), new ReportService());
    }

    TraceCommand(final CliArguments arguments, final ImporterService importerService,
            final Tracer tracer, final ReportService reportService)
    {
        super(arguments, importerService);
        this.tracer = tracer;
        this.reportService = reportService;
    }

    @Override
    protected void processSpecificationItemStream(final Stream<SpecificationItem> items)
    {
        final Linker linker = new Linker(items.collect(Collectors.toList()));
        final List<LinkedSpecificationItem> linkedItems = linker.link();
        final Trace traceResult = this.tracer.trace(linkedItems);
        final ReportVerbosity verbosity = this.arguments.getReportVerbosity() == null
                ? ReportVerbosity.FAILURE_DETAILS : this.arguments.getReportVerbosity();
        this.reportService.generateReport(traceResult, this.arguments.getOutputFile(), verbosity,
                this.arguments.getNewline());

    }
}
