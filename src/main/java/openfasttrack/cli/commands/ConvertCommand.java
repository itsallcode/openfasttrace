package openfasttrack.cli.commands;

import java.util.stream.Stream;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.ExporterService;
import openfasttrack.importer.ImporterService;

/**
 * Handler for specification item conversion CLI command.
 */
public class ConvertCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "convert";
    private final ExporterService exporterService;

    /**
     * Create a {@link ConvertCommand}
     * 
     * @param arguments
     *            the command line arguments
     */
    public ConvertCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new ExporterService());
    }

    ConvertCommand(final CliArguments arguments, final ImporterService importerService,
            final ExporterService exporterService)
    {
        super(arguments, importerService);
        this.exporterService = exporterService;
    }

    @Override
    protected void processSpecificationItemStream(
            final Stream<LinkedSpecificationItem> linkedSpecItemStream)
    {
        this.exporterService.exportFile(linkedSpecItemStream, this.arguments.getOutputFormat(),
                this.arguments.getOutputFile());
    }
}
