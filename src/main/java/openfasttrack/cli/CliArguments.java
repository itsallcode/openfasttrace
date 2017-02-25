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
import java.util.Arrays;
import java.util.List;

import openfasttrack.core.Newline;
import openfasttrack.report.ReportVerbosity;

/**
 * This class implements a parameter object into which the command line parser
 * injects the results of parsing the command line.
 */
public class CliArguments
{
    private static final String CURRENT_DIRECTORY = ".";
    private List<String> unnamedValues;
    private String outputFile;
    private String outputFormat;
    private ReportVerbosity reportVerbosity;
    private Newline newline = Newline.fromRepresentation(System.lineSeparator());

    /**
     * Get the output file path
     * 
     * @return output file path
     */
    public Path getOutputFile()
    {
        if (this.outputFile == null)
        {
            return null;
        }
        return Paths.get(this.outputFile);
    }

    /**
     * Set the output file path
     * 
     * @param outputFile
     *            output file path
     */
    public void setOutputFile(final String outputFile)
    {
        this.outputFile = outputFile;
    }

    /**
     * Get the OFT command (verb).
     * 
     * @return the OFT command
     */
    public String getCommand()
    {
        if (this.unnamedValues == null || this.unnamedValues.isEmpty())
        {
            return null;
        }
        return this.unnamedValues.get(0);
    }

    /**
     * Get input file or directory path(s)
     * 
     * @return input paths
     */
    public List<String> getInputs()
    {
        if (this.unnamedValues == null || this.unnamedValues.size() <= 1)
        {
            return Arrays.asList(CURRENT_DIRECTORY);
        }
        return this.unnamedValues.subList(1, this.unnamedValues.size());
    }

    /**
     * Set all parameter values that are not led in by a command line parameter
     * name
     * 
     * @param unnamedValues
     *            list of unnamed values
     */
    public void setUnnamedValues(final List<String> unnamedValues)
    {
        this.unnamedValues = unnamedValues;
    }

    /**
     * Get the output format for an export or report
     * 
     * @return the output format
     */
    public String getOutputFormat()
    {
        return this.outputFormat;
    }

    /**
     * Set the output format for an export or report
     * 
     * @param outputFormat
     *            the output format
     */
    public void setOutputFormat(final String outputFormat)
    {
        this.outputFormat = outputFormat;
    }

    /**
     * Set the output format for an export or report
     * 
     * @param outputFormat
     *            the output format
     */
    public void setO(final String outputFormat)
    {
        setOutputFormat(outputFormat);
    }

    /**
     * Get the report verbosity of a report
     * 
     * @return the report verbosity of a report
     */
    public ReportVerbosity getReportVerbosity()
    {
        return this.reportVerbosity;
    }

    /**
     * Set the report verbosity for a report
     * 
     * @param reportVerbosity
     *            the verbosity of the report
     */
    public void setReportVerbosity(final ReportVerbosity reportVerbosity)
    {
        this.reportVerbosity = reportVerbosity;
    }

    /**
     * Set the report verbosity for a report
     * 
     * @param reportVerbosity
     *            the verbosity of the report
     */
    public void setV(final ReportVerbosity reportVerbosity)
    {
        setReportVerbosity(reportVerbosity);
    }

    /**
     * Get the line separator representation for output files
     * 
     * @return the line separator
     */
    public Newline getNewline()
    {
        return this.newline;
    }

    /**
     * Set the line separator representation for output files
     * 
     * @param newline
     *            the line separator
     */
    public void setNewline(final Newline newline)
    {
        this.newline = newline;
    }

    /**
     * Set the line separator representation for output files
     * 
     * @param newline
     *            the line separator
     */
    public void setN(final Newline newline)
    {
        setNewline(newline);
    }
}
