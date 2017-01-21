package openfasttrack.cli.commands;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
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
    protected void processSpecificationItemStream(
            final Stream<LinkedSpecificationItem> linkedSpecItems)
    {
        final Trace traceResult = this.tracer.trace(linkedSpecItems.collect(Collectors.toList()));
        final ReportVerbosity verbosity = this.arguments.getReportVerbosity() == null
                ? ReportVerbosity.FAILURE_DETAILS : this.arguments.getReportVerbosity();
        this.reportService.generateReport(traceResult, this.arguments.getOutputFile(), verbosity);

    }
}
