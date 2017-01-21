package openfasttrack.cli.commands;

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

import java.util.stream.Stream;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.exporter.ExporterService;
import openfasttrack.importer.ImporterService;

/**
 * Handler for specification item conversion CLI command.
 */
public class ConvertCommand extends AbstractCommand
{
    public static final String COMMAND_NAME = "convert";
    private final ExporterService exporterService;

    /**
     * Create a {@link ConvertCommand}
     * 
     * @param arguments
     *            the command line arguments
     */
    public ConvertCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new ExporterService());
    }

    ConvertCommand(final CliArguments arguments, final ImporterService importerService,
            final ExporterService exporterService)
    {
        super(arguments, importerService);
        this.exporterService = exporterService;
    }

    @Override
    protected void processSpecificationItemStream(
            final Stream<LinkedSpecificationItem> linkedSpecItemStream)
    {
        this.exporterService.exportFile(linkedSpecItemStream, this.arguments.getOutputFormat(),
                this.arguments.getOutputFile());
    }
}
