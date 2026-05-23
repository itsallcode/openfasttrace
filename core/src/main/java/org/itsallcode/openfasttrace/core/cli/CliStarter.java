package org.itsallcode.openfasttrace.core.cli;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.core.cli.commands.*;
import org.itsallcode.openfasttrace.core.cli.logging.LoggingConfigurator;

import static org.itsallcode.openfasttrace.core.cli.ExitStatus.*;

/**
 * The main entry point class for the command line application.
 */
public class CliStarter
{
    private static final String MISSING_COMMAND = "missing";
    private final CliArguments arguments;

    /**
     * Create a new cli starter.
     * 
     * @param arguments
     *            the arguments for the starter.
     */
    public CliStarter(final CliArguments arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Main entry point for the command line application
     * 
     * @param args
     *            command line parameters
     */
    public static void main(final String[] args)
    {
        final DirectoryService directoryService = new StandardDirectoryService();
        mainDelegate(args, directoryService);
    }

    /**
     * Auxiliary entry point to the command line application that allows
     * injection of a {@link DirectoryService}.
     * 
     * @param args
     *            command line arguments.
     * @param directoryService
     *            directory service for getting the current directory. This
     *            allows injecting a mock in unit tests.
     */
    public static void mainDelegate(final String[] args, final DirectoryService directoryService)
    {
        final CliArguments arguments = parseCommandLineArguments(args, directoryService);
        final ArgumentValidator validator = new ArgumentValidator(arguments);
        if (validator.isValid())
        {
            LoggingConfigurator.create(arguments).configureLogging();
            new CliStarter(arguments).run();
        }
        else
        {
            printToStdError(
                    "oft: " + validator.getError() + "\n" + validator.getSuggestion() + "\n");
            exit(CLI_ERROR);
        }
    }

    @SuppressWarnings("java:S1166") // Exceptions are reported to the user
    private static CliArguments parseCommandLineArguments(final String[] args,
            final DirectoryService directoryService)
    {
        final CliArguments arguments = new CliArguments(directoryService);
        try
        {
            new CommandLineInterpreter(args, arguments).parse();
        }
        catch (final CliException e)
        {
            printToStdError("oft: " + e.getMessage());
            exit(CLI_ERROR);
        }
        return arguments;
    }

    // Writing to standard error by intention
    @SuppressWarnings("squid:S106")
    private static void printToStdError(final String message)
    {
        System.err.println(message);
    }

    /**
     * Process the command line arguments and execute the commands.
     */
    // [impl->dsn~cli.command-selection~1]
    public void run()
    {
        final String command = this.arguments.isHelpSet()
                ? HelpCommand.COMMAND_NAME
                : this.arguments.getCommand().orElse(MISSING_COMMAND);
        ExitStatus exitStatus;
        switch (command)
        {
        case ConvertCommand.COMMAND_NAME:
            exitStatus = new ConvertCommand(this.arguments).run() ? OK : FAILURE;
            break;
        case TraceCommand.COMMAND_NAME:
            exitStatus = new TraceCommand(this.arguments).run() ? OK : FAILURE;
            break;
        case HelpCommand.COMMAND_NAME:
            exitStatus = new HelpCommand(true).run() ? OK : FAILURE;
            break;
        case MISSING_COMMAND:
        default:
            new HelpCommand(false).run();
            printToStdError("Compand missing trying to execute OFT. Choose one of: trace, convert, help");
            exitStatus = CLI_ERROR;
            break;
        }
        exit(exitStatus);
    }

    // [impl->dsn~cli.tracing.exit-status~1]
    @SuppressWarnings("java:S1147") // Calling System.exit() intentionally
    private static void exit(final ExitStatus exitStatus)
    {
        System.exit(exitStatus.getCode());
    }
}
