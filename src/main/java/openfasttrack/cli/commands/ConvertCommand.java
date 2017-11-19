package openfasttrack.cli.commands;

import java.nio.file.Path;

import openfasttrack.Converter;
import openfasttrack.cli.CliArguments;
import openfasttrack.mode.ConvertMode;

/**
 * Handler for specification item conversion CLI command.
 */
public class ConvertCommand extends AbstractCommand implements Performable
{
    public static final String COMMAND_NAME = "convert";

    /**
     * Create a {@link ConvertCommand}.
     * 
     * @param arguments
     *            command line arguments.
     */
    public ConvertCommand(final CliArguments arguments)
    {
        super(arguments);
    }

    @Override
    public boolean run()
    {
        final Converter converter = createConverter();
        convert(converter);
        return true;
    }

    private Converter createConverter()
    {
        final Converter converter = new ConvertMode();
        converter.addInputs(toPaths(this.arguments.getInputs())) //
                .setNewline(this.arguments.getNewline());
        return converter;
    }

    private void convert(final Converter converter)
    {
        final Path outputPath = this.arguments.getOutputPath();
        converter.convertToFileInFormat(outputPath, this.arguments.getOutputFormat());
    }
}
