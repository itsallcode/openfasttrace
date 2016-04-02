package openfasttrack.cli.commands;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.exporter.ExporterService;
import openfasttrack.importer.ImporterService;

public class ConvertCommand
{
    public final static String COMMAND_NAME = "convert";
    private final CliArguments arguments;
    private final ImporterService importerService;
    private final ExporterService exporterService;

    public ConvertCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new ExporterService());
    }

    ConvertCommand(final CliArguments arguments, final ImporterService importerService,
            final ExporterService exporterService)
    {
        this.arguments = arguments;
        this.importerService = importerService;
        this.exporterService = exporterService;
    }

    public void start()
    {
        final Map<SpecificationItemId, SpecificationItem> specItems = this.importerService
                .importRecursiveDir(this.arguments.getInputDir(), "**/*");
        final List<LinkedSpecificationItem> linkedSpecItems = specItems.values() //
                .stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(toList());
        this.exporterService.exportFile(linkedSpecItems, this.arguments.getOutputFormat(),
                this.arguments.getOutputFile());
    }
}
