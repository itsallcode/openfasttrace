
package org.itsallcode.openfasttrace.core.cli.commands;

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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.core.SpecificationItem;
import org.itsallcode.openfasttrace.api.importer.ImportSettings;
import org.itsallcode.openfasttrace.core.Oft;
import org.itsallcode.openfasttrace.core.cli.CliArguments;

/**
 * This class is the abstract base class for all commands that process a list of
 * input files and directories.
 */
public abstract class AbstractCommand implements Performable
{
    /** The command line arguments given by the user. */
    protected CliArguments arguments;
    /** The OFT instance for executing commands. */
    protected final Oft oft;

    /**
     * Creates a new instance.
     * 
     * @param arguments
     *            the command line arguments.
     */
    protected AbstractCommand(final CliArguments arguments)
    {
        this.arguments = arguments;
        this.oft = Oft.create();
    }

    private List<Path> toPaths(final List<String> inputs)
    {
        final List<Path> inputsAsPaths = new ArrayList<>();
        for (final String input : inputs)
        {
            inputsAsPaths.add(Paths.get(input));
        }
        return inputsAsPaths;
    }

    private FilterSettings createFilterSettingsFromArguments()
    {
        final FilterSettings.Builder builder = new FilterSettings.Builder();
        setAttributeTypeFilter(builder);
        setTagFilter(builder);
        return builder.build();
    }

    private void setAttributeTypeFilter(final FilterSettings.Builder builder)
    {
        if (this.arguments.getWantedArtifactTypes() != null
                && !this.arguments.getWantedArtifactTypes().isEmpty())
        {
            builder.artifactTypes(this.arguments.getWantedArtifactTypes());
        }
    }

    private void setTagFilter(final FilterSettings.Builder builder)
    {
        final Set<String> wantedTags = this.arguments.getWantedTags();
        if (wantedTags != null && !wantedTags.isEmpty())
        {
            if (wantedTags.contains(CliArguments.NO_TAGS_MARKER))
            {
                builder.withoutTags(true);
                wantedTags.remove(CliArguments.NO_TAGS_MARKER);
            }
            else
            {
                builder.withoutTags(false);
            }
            builder.tags(wantedTags);
        }
    }

    /**
     * Import items using filter settings from command line arguments.
     * 
     * @return the imported items.
     */
    protected List<SpecificationItem> importItems()
    {
        final ImportSettings importSettings = ImportSettings
                .builder()
                .addInputs(this.toPaths(this.arguments.getInputs()))
                .filter(createFilterSettingsFromArguments())
                .build();
        return this.oft.importItems(importSettings);
    }
}