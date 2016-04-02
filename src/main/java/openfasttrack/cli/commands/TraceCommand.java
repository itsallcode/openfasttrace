package openfasttrack.cli.commands;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
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
        final Map<SpecificationItemId, SpecificationItem> specItems = this.importerService
                .importRecursiveDir(this.arguments.getInputDir(), "*");
        final List<LinkedSpecificationItem> linkedSpecItems = specItems.values() //
                .stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(toList());
        final Trace traceResult = this.tracer.trace(linkedSpecItems);
        this.reportService.generateReport(traceResult, this.arguments.getOutputFile(),
                ReportVerbosity.ALL);
    }
}
