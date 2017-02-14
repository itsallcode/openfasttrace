package openfasttrack.cli;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 - 2017 hamstercommunity
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

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;

import openfasttrack.cli.commands.ConvertCommand;
import openfasttrack.cli.commands.TraceCommand;
import openfasttrack.exporter.ExporterFactoryLoader;
import openfasttrack.report.ReportVerbosity;

public class ArgumentValidator
{
    private static final List<String> AVAILABLE_COMMANDS = asList(ConvertCommand.COMMAND_NAME,
            TraceCommand.COMMAND_NAME);

    private final CliArguments arguments;
    private String error;
    private String suggestion;
    private final boolean valid;

    public ArgumentValidator(final CliArguments arguments)
    {
        this.arguments = arguments;
        this.valid = validate();
    }

    private boolean validate()
    {
        final String command = this.arguments.getCommand();
        boolean valid = false;
        if (command == null)
        {
            this.error = "Missing command";
            this.suggestion = "Add one of " + listCommands();
        }
        else if (!AVAILABLE_COMMANDS.contains(command))
        {
            this.error = "'" + command + "' is not an OFT command.";
            this.suggestion = "Choose one of " + listCommands() + ".";
        }
        else if (TraceCommand.COMMAND_NAME.equals(command))
        {
            valid = validateTraceCommand();
        }
        else if (ConvertCommand.COMMAND_NAME.equals(command))
        {
            valid = validateConvertCommand();
        }
        else
        {
            valid = true;
        }

        return valid;
    }

    private boolean validateTraceCommand()
    {
        boolean valid = false;
        if (this.arguments.getReportVerbosity() == ReportVerbosity.QUIET
                && this.arguments.getOutputFile() != null)
        {
            this.error = "combining report verbosity 'quiet' and ouput to file is not supported.";
            this.suggestion = "remove output file parameter.";
        }
        else
        {
            valid = true;
        }
        return valid;
    }

    private boolean validateConvertCommand()
    {
        boolean valid = false;
        final String format = this.arguments.getOutputFormat();
        if (format != null && !new ExporterFactoryLoader().isFormatSupported(format))
        {
            this.error = "export format '" + format + "' is not supported.";
        }
        else
        {
            valid = true;
        }
        return valid;
    }

    private String listCommands()
    {
        return AVAILABLE_COMMANDS.stream().map(text -> "'" + text + "'")
                .collect(Collectors.joining(","));
    }

    public boolean isValid()
    {
        return this.valid;
    }

    public String getError()
    {
        return this.error;
    }

    public String getSuggestion()
    {
        return this.suggestion;
    }

}
