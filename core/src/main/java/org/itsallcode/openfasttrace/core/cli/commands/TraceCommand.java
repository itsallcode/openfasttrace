
package org.itsallcode.openfasttrace.core.cli.commands;

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
                .colorScheme(this.arguments.getColorScheme()) //
                .build();
    }
}