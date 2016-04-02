package openfasttrack.cli.commands;

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

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.core.SpecificationItem;
import openfasttrack.core.SpecificationItemId;
import openfasttrack.exporter.ExporterService;
import openfasttrack.importer.ImporterService;

public class ConvertCommand
{
    public final static String COMMAND_NAME = "convert";
    private final CliArguments arguments;
    private final ImporterService importerService;
    private final ExporterService exporterService;

    public ConvertCommand(final CliArguments arguments)
    {
        this(arguments, new ImporterService(), new ExporterService());
    }

    ConvertCommand(final CliArguments arguments, final ImporterService importerService,
            final ExporterService exporterService)
    {
        this.arguments = arguments;
        this.importerService = importerService;
        this.exporterService = exporterService;
    }

    public void start()
    {
        final Map<SpecificationItemId, SpecificationItem> specItems = this.importerService
                .importRecursiveDir(this.arguments.getInputDir(), "**/*");
        final List<LinkedSpecificationItem> linkedSpecItems = specItems.values() //
                .stream() //
                .map(LinkedSpecificationItem::new) //
                .collect(toList());
        this.exporterService.exportFile(linkedSpecItems, this.arguments.getOutputFormat(),
                this.arguments.getOutputFile());
    }
}
