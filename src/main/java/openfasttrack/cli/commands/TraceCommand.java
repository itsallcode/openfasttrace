package openfasttrack.cli.commands;

import java.nio.file.Path;

import openfasttrack.Reporter;
import openfasttrack.cli.CliArguments;
import openfasttrack.core.Trace;
import openfasttrack.mode.ReportMode;

/**
 * Handler for requirement tracing CLI command.
 */
public class TraceCommand extends AbstractCommand
{
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
        final Reporter reporter = createReporter();
        final Trace trace = report(reporter);
        return trace.isAllCovered();
    }

    private Reporter createReporter()
    {
        final Reporter reporter = new ReportMode();
        reporter.addInputs(toPaths(this.arguments.getInputs())) //
                .setNewline(this.arguments.getNewline())
                .setReportVerbosity(this.arguments.getReportVerbosity());
        return reporter;
    }

    private Trace report(final Reporter reporter)
    {
        final Trace trace = reporter.trace();
        final Path outputPath = this.arguments.getOutputPath();
        if (null == outputPath)
        {
            reporter.reportToStdOutInFormat(trace, this.arguments.getOutputFormat());

        }
        else
        {
            reporter.reportToFileInFormat(trace, this.arguments.getOutputPath(),
                    this.arguments.getOutputFormat());
        }
        return trace;
    }
}
