
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.FilterSettings;
import org.itsallcode.openfasttrace.cli.CliArguments;

/**
 * This class is the abstract base class for all commands that process a list of
 * input files and directories.
 */
public abstract class AbstractCommand implements Performable
{
    protected CliArguments arguments;

    protected AbstractCommand(final CliArguments arguments)
    {
        this.arguments = arguments;
    }

    public List<Path> toPaths(final List<String> inputs)
    {
        final List<Path> inputsAsPaths = new ArrayList<>();
        for (final String input : inputs)
        {
            inputsAsPaths.add(Paths.get(input));
        }
        return inputsAsPaths;
    }

    protected FilterSettings createFilterSettingsFromArguments()
    {
        return new FilterSettings.Builder() //
                .artifactTypes(this.arguments.getWantedArtifactTypes()) //
                .build();
    }
}
