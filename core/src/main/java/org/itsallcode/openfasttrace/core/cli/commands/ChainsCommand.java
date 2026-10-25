package org.itsallcode.openfasttrace.core.cli.commands;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.chains.*;
import org.itsallcode.openfasttrace.core.cli.CliArguments;

/**
 * Handler for tracing chains CLI command.
 */
// [impl->dsn~cli.command-selection~1]
// [impl->dsn~cli.chains.output-format~1]
// [impl->dsn~cli.chains.default-output-format~1]
public class ChainsCommand extends AbstractCommand
{
    /** The command line action for running this command. */
    public static final String COMMAND_NAME = "chains";

    /** Default output format for chains. */
    public static final String DEFAULT_OUTPUT_FORMAT = "chains";

    /**
     * Create a {@link ChainsCommand}.
     * 
     * @param arguments
     *            command line arguments.
     */
    public ChainsCommand(final CliArguments arguments)
    {
        super(arguments);
    }

    @Override
    @SuppressWarnings("java:S106") // Output directed to standard out
    public boolean run()
    {
        final List<SpecificationItem> items = importItems();
        final TracingGraph graph = TracingGraph.fromItems(items);
        final ChainsFormat format = ChainsFormat.fromString(this.arguments.getOutputFormat());
        final String rendered = new TracingGraphRenderer(graph, format, this.arguments.getNewline()).render();
        System.out.print(rendered);
        return true;
    }
}
