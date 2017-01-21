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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import openfasttrack.cli.CliArguments;
import openfasttrack.core.LinkedSpecificationItem;
import openfasttrack.importer.ImporterService;

/**
 * This class is the abstract base class for all commands that process a list of
 * input files and directories.
 */
public abstract class AbstractCommand
{

    protected final CliArguments arguments;
    protected final ImporterService importerService;

    /**
     * Construct the {@link AbstractCommand}.
     * 
     * @param arguments
     *            the command line arguments
     * @param importerService
     *            the importer service
     */
    public AbstractCommand(final CliArguments arguments, final ImporterService importerService)
    {
        this.arguments = arguments;
        this.importerService = importerService;
    }

    /**
     * Run the importer on the list of inputs and process the results.
     */
    public void start()
    {
        final Stream<LinkedSpecificationItem> linkedSpecItems = importItemsFromPaths(
                getNormalizedPaths());
        processSpecificationItemStream(linkedSpecItems);
    }

    private Stream<LinkedSpecificationItem> importItemsFromPaths(final List<Path> paths)
    {
        return this.importerService.createImporter() //
                .importAny(paths) //
                .getImportedItems() //
                .stream() //
                .map(LinkedSpecificationItem::new);
    }

    private List<Path> getNormalizedPaths()
    {
        return this.arguments.getInputs().stream() //
                .map(input -> Paths.get(input).toAbsolutePath().normalize()) //
                .collect(Collectors.toList());
    }

    /**
     * The actual processing of the imported {@link LinkedSpecificationItem}
     * list.
     * 
     * @param linkedSpecItems
     *            specification items to be processed
     */
    abstract protected void processSpecificationItemStream(
            Stream<LinkedSpecificationItem> linkedSpecItems);
}