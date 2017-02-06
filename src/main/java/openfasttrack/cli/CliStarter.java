package openfasttrack.cli;

/*
 * #%L
 * OpenFastTrack
 * %%
 * Copyright (C) 2016 hamstercommunity
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

import openfasttrack.cli.commands.ConvertCommand;
import openfasttrack.cli.commands.Performable;
import openfasttrack.cli.commands.TraceCommand;

public class CliStarter
{
    private static final List<String> AVAILABLE_COMMANDS = asList(ConvertCommand.COMMAND_NAME,
            TraceCommand.COMMAND_NAME);

    private final CliArguments arguments;

    CliStarter(final CliArguments arguments)
    {
        this.arguments = arguments;
    }

    public static void main(final String[] args)
    {
        final CliArguments cliArguments = new CliArguments();
        new CommandLineInterpreter(args, cliArguments).parse();
        new CliStarter(cliArguments).run();
    }

    void run()
    {
        if (this.arguments.getCommand() == null)
        {
            throw new CliException("No command given, expected one of " + AVAILABLE_COMMANDS);
        }
        Performable performable;
        switch (this.arguments.getCommand())
        {
        case ConvertCommand.COMMAND_NAME:
            performable = new ConvertCommand(this.arguments);
            break;
        case TraceCommand.COMMAND_NAME:
            performable = new TraceCommand(this.arguments);
            break;
        default:
            throw new CliException("Invalid command '" + this.arguments.getCommand()
                    + "' given, expected one of " + AVAILABLE_COMMANDS);
        }
        final ExitStatus status = ExitStatus.fromBoolean(performable.run());
        System.exit(status.getCode());
    }
}
