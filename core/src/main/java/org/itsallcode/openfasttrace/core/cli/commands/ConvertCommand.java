
package org.itsallcode.openfasttrace.core.cli.commands;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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
