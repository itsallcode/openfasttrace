
package org.itsallcode.openfasttrace.core.cli.commands;

import java.util.List;

import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.ExportSettings;
import org.itsallcode.openfasttrace.core.cli.CliArguments;

/**
 * Handler for specification item conversion CLI command.
 */
public class ConvertCommand extends AbstractCommand implements Performable
{
    /** The command line action for running this command. */
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
        final List<SpecificationItem> items = importItems();
        convert(items);
        return true;
    }

    private void convert(final List<SpecificationItem> items)
    {
        final ExportSettings exportSettings = createExportSettingsFromArguments();
        this.oft.exportToPath(items, this.arguments.getOutputPath(), exportSettings);
    }

    private ExportSettings createExportSettingsFromArguments()
    {
        return ExportSettings.builder() //
                .newline(this.arguments.getNewline()) //
                .outputFormat(this.arguments.getOutputFormat()) //
                .build();
    }
}
