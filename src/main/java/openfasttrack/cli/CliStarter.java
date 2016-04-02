package openfasttrack.cli;

import static java.util.Arrays.asList;

import java.util.List;

import openfasttrack.cli.commands.ConvertCommand;
import openfasttrack.cli.commands.TraceCommand;

public class CliStarter
{
    private static List<String> AVAILABLE_COMMANDS = asList(ConvertCommand.COMMAND_NAME,
            TraceCommand.COMMAND_NAME);

    private final CliArguments arguments;

    public static void main(final String[] args)
    {
        final CliArguments cliArguments = new CliArguments();
        new CommandLineInterpreter(args, cliArguments).parse();
        new CliStarter(cliArguments).start();
    }

    CliStarter(final CliArguments arguments)
    {
        this.arguments = arguments;
    }

    void start()
    {
        if (this.arguments.getCommand() == null)
        {
            throw new CliException("No command given, expected one of " + AVAILABLE_COMMANDS);
        }
        switch (this.arguments.getCommand())
        {
        case ConvertCommand.COMMAND_NAME:
            new ConvertCommand(this.arguments).start();
            break;
        case TraceCommand.COMMAND_NAME:
            new TraceCommand(this.arguments).start();
            break;

        default:
            throw new CliException("Invalid command '" + this.arguments.getCommand()
                    + "' given, expected one of " + AVAILABLE_COMMANDS);
        }
    }
}
