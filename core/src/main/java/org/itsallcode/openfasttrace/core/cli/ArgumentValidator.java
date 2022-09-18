package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.itsallcode.openfasttrace.core.cli.commands.*;
import org.itsallcode.openfasttrace.core.exporter.ExporterFactoryLoader;

/**
 * The {@link ArgumentValidator} checks whether the command line arguments given
 * by the user contain valid values and are valid in combination.
 */
public class ArgumentValidator
{
    private static final List<String> AVAILABLE_COMMANDS = asList(HelpCommand.COMMAND_NAME, ConvertCommand.COMMAND_NAME,
            TraceCommand.COMMAND_NAME);

    private final CliArguments arguments;
    private String error = "";
    private String suggestion = "";
    private final boolean valid;

    /**
     * Create a new {@link ArgumentValidator}
     * 
     * @param arguments
     *            the command line arguments to be validated
     */
    public ArgumentValidator(final CliArguments arguments)
    {
        this.arguments = arguments;
        this.valid = validate();
    }

    private boolean validate()
    {
        final Optional<String> command = this.arguments.getCommand();
        boolean ok = false;
        if (command.isEmpty())
        {
            this.error = "Missing command";
            this.suggestion = "Add one of " + listCommands();
        }
        else if (HelpCommand.COMMAND_NAME.equals(command.get()))
        {
            ok = true;
        }
        else if (TraceCommand.COMMAND_NAME.equals(command.get()))
        {
            ok = validateTraceCommand();
        }
        else if (ConvertCommand.COMMAND_NAME.equals(command.get()))
        {
            ok = validateConvertCommand();
        }
        else
        {
            this.error = "'" + command.orElse(null) + "' is not an OFT command.";
            this.suggestion = "Choose one of " + listCommands() + ".";
        }

        return ok;
    }

    private boolean validateTraceCommand()
    {
        boolean ok = false;
        if (this.arguments.getReportVerbosity() == ReportVerbosity.QUIET
                && this.arguments.getOutputPath() != null)
        {
            this.error = "combining stream verbosity 'quiet' and output to file is not supported.";
            this.suggestion = "remove output file parameter.";
        }
        else
        {
            ok = true;
        }
        return ok;
    }

    private boolean validateConvertCommand()
    {
        boolean ok = false;
        final String format = this.arguments.getOutputFormat();
        if (format != null && !new ExporterFactoryLoader(null).isFormatSupported(format))
        {
            this.error = "export format '" + format + "' is not supported.";
        }
        else
        {
            ok = true;
        }
        return ok;
    }

    private String listCommands()
    {
        return AVAILABLE_COMMANDS.stream().map(text -> "'" + text + "'")
                .collect(Collectors.joining(","));
    }

    /**
     * Check if the command line arguments are valid
     * 
     * @return <code>true</code> if the command line arguments are valid
     */
    public boolean isValid()
    {
        return this.valid;
    }

    /**
     * Get the error message
     * 
     * @return the error message
     */
    public String getError()
    {
        return this.error;
    }

    /**
     * Get a suggestion on how to solve an error
     * 
     * @return the error resolution suggestion
     */
    public String getSuggestion()
    {
        return this.suggestion;
    }
}
