package org.itsallcode.openfasttrace.core.cli;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;
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
        this.arguments = Objects.requireNonNull(arguments);
        this.valid = validate();
    }

    private boolean validate()
    {
        final Optional<String> command = this.arguments.getCommand();
        if (this.arguments.isHelpSet())
        {
            return true;
        }
        else if (command.isEmpty())
        {
            this.error = "Missing command";
            this.suggestion = "Add one of " + listCommands();
            return false;
        }
        else if (HelpCommand.COMMAND_NAME.equals(command.get()))
        {
            return true;
        }
        else if (TraceCommand.COMMAND_NAME.equals(command.get()))
        {
            return validateTraceCommand();
        }
        else if (ConvertCommand.COMMAND_NAME.equals(command.get()))
        {
            return validateConvertCommand();
        }
        else
        {
            this.error = "'" + command.orElse(null) + "' is not an OFT command.";
            this.suggestion = "Choose one of " + listCommands() + ".";
            return false;
        }
    }

    private boolean validateTraceCommand()
    {
        if (this.arguments.getReportVerbosity() == ReportVerbosity.QUIET
                && this.arguments.getOutputPath() != null)
        {
            this.error = "combining stream verbosity 'quiet' and output to file is not supported.";
            this.suggestion = "remove output file parameter.";
            return false;
        }
        return true;
    }

    private boolean validateConvertCommand()
    {
        final String format = Objects.requireNonNull(this.arguments.getOutputFormat(), "<null>");
        if (!new ExporterFactoryLoader(null).isFormatSupported(format))
        {
            this.error = "export format '" + format + "' is not supported.";
            return false;
        }
        return true;
    }

    private static String listCommands()
    {
        return AVAILABLE_COMMANDS.stream().map(text -> "'" + text + "'")
                .collect(Collectors.joining(","));
    }

    /**
     * Check if the command line arguments are valid
     * 
     * @return {@code true} if the command line arguments are valid
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
