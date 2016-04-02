package openfasttrack.cli;

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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CliArguments
{
    private List<String> unnamedValues;
    private String inputDir;
    private String outputFile;
    private String outputFormat;

    public Path getInputDir()
    {
        if (this.inputDir == null)
        {
            throw new MissingArgumentException("inputDir");
        }
        return Paths.get(this.inputDir);
    }

    public void setInputDir(final String inputDir)
    {
        this.inputDir = inputDir;
    }

    public Path getOutputFile()
    {
        if (this.outputFile == null)
        {
            throw new MissingArgumentException("outputFile");
        }
        return Paths.get(this.outputFile);
    }

    public void setOutputFile(final String outputFile)
    {
        this.outputFile = outputFile;
    }

    public String getCommand()
    {
        if (this.unnamedValues != null && this.unnamedValues.size() > 0)
        {
            return this.unnamedValues.get(0);
        }
        return null;
    }

    public void setUnnamedValues(final List<String> unnamedValues)
    {
        this.unnamedValues = unnamedValues;
    }

    public String getOutputFormat()
    {
        if (this.outputFormat == null)
        {
            throw new MissingArgumentException("outputFormat");
        }
        return this.outputFormat;
    }

    public void setOutputFormat(final String outputFormat)
    {
        this.outputFormat = outputFormat;
    }
}
