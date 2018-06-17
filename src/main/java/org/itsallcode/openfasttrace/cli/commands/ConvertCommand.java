
package org.itsallcode.openfasttrace.cli.commands;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
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

import java.nio.file.Path;

import org.itsallcode.openfasttrace.Converter;
import org.itsallcode.openfasttrace.cli.CliArguments;
import org.itsallcode.openfasttrace.mode.ConvertMode;

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
                .setFilters(createFilterSettingsFromArguments()) //
                .setNewline(this.arguments.getNewline());
        return converter;
    }

    private void convert(final Converter converter)
    {
        final Path outputPath = this.arguments.getOutputPath();
        converter.convertToFileInFormat(outputPath, this.arguments.getOutputFormat());
    }
}
