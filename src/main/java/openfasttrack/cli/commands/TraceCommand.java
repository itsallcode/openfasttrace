package openfasttrack.cli.commands;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import static java.util.stream.Collectors.toList;

import java.util.List;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.Trace;
import openfasttrack.core.Tracer;
import openfasttrack.importer.ImporterService;
import openfasttrack.report.ReportService;
import openfasttrack.report.ReportVerbosity;

public class TraceCommand
{
    public final static String COMMAND_NAME = "trace";
    private final CliArguments arguments;
    private final ImporterService importerService;
    private final ReportService reportService;
    private final Tracer tracer;

    public TraceCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new Tracer(), new ReportService());
    }

    TraceCommand(final CliArguments arguments, final ImporterService importerService,
            final Tracer tracer, final ReportService reportService)
    {
        this.arguments = arguments;
        this.importerService = importerService;
        this.tracer = tracer;
        this.reportService = reportService;
    }

    public void start()
    {
        final List<LinkedSpecificationItem> linkedSpecItems = this.importerService.createImporter() //
                .importRecursiveDir(this.arguments.getInputDir(), "**/*") //
                .getImportedItems() //
                .values() //
                .stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(toList());
        final Trace traceResult = this.tracer.trace(linkedSpecItems);
        this.reportService.generateReport(traceResult, this.arguments.getOutputFile(),
                ReportVerbosity.FAILURE_DETAILS);
    }
}
