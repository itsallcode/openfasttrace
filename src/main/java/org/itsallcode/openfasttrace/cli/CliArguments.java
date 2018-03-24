package org.itsallcode.openfasttrace.cli;

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

import static java.util.Arrays.asList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.itsallcode.openfasttrace.cli.commands.ConvertCommand;
import org.itsallcode.openfasttrace.cli.commands.TraceCommand;
import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.exporter.ExporterConstants;
import org.itsallcode.openfasttrace.report.ReportConstants;
import org.itsallcode.openfasttrace.report.ReportVerbosity;

/**
 * This class implements a parameter object into which the command line parser
 * injects the results of parsing the command line.
 */
public class CliArguments
{
    private static final String CURRENT_DIRECTORY = ".";
    // [impl->dsn~cli.default-newline-format~1]
    private Newline newline = Newline.fromRepresentation(System.lineSeparator());
    private List<String> unnamedValues;
    private Path outputFile;
    private String outputFormat;
    private ReportVerbosity reportVerbosity;
    private Set<String> wantedArtifactTypes = Collections.emptySet();
    private Set<String> wantedTags = Collections.emptySet();

    /**
     * Get the output file path
     * 
     * @return output file path
     */
    public Path getOutputPath()
    {
        if (this.outputFile == null)
        {
            return null;
        }
        return this.outputFile;
    }

    /**
     * Set the output file path
     * 
     * @param outputFile
     *            output file path
     */
    public void setOutputFile(final String outputFile)
    {
        this.outputFile = Paths.get(outputFile);
    }

    /**
     * Set the output file path
     * 
     * @param outputFile
     *            output file path
     */
    public void setF(final String outputFile)
    {
        setOutputFile(outputFile);
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
    // [impl->dsn~cli.input-file-selection~1]
    // [impl->dsn~cli.default-input~1]
    public List<String> getInputs()
    {
        if (this.unnamedValues == null || this.unnamedValues.size() <= 1)
        {
            return asList(CURRENT_DIRECTORY);
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
    // [impl->dsn~cli.tracing.default-format~1]
    // [impl->dsn~cli.conversion.default-output-format~1]]
    // [impl->dsn~cli.tracing.output-format~1]
    public String getOutputFormat()
    {
        if (this.outputFormat == null)
        {
            if (this.getCommand().equals(TraceCommand.COMMAND_NAME))
            {
                return ReportConstants.DEFAULT_REPORT_FORMAT;
            }
            else if (this.getCommand().equals(ConvertCommand.COMMAND_NAME))
            {
                return ExporterConstants.DEFAULT_OUTPUT_FORMAT;
            }
            else
            {
                throw new IllegalStateException("Illegal command \"" + this.getCommand()
                        + "\" encountered trying to set default output format.");
            }
        }
        return this.outputFormat;
    }

    /**
     * Set the output format for an export or report
     * 
     * @param outputFormat
     *            the output format
     */
    // [impl->dsn~cli.conversion.output-format~1]
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
        return (null == this.reportVerbosity) ? ReportConstants.DEFAULT_REPORT_VERBOSITY
                : this.reportVerbosity;
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

    /**
     * Get a list of artifact types to be applied as filter during import
     * 
     * @return list of wanted artifact types
     */
    public Set<String> getWantedArtifactTypes()
    {
        return this.wantedArtifactTypes;
    }

    /**
     * Set a list of artifact types to be applied as filter during import
     * 
     * @param artifactTypes
     *            list of wanted artifact types
     */
    public void setWantedArtifactTypes(final String artifactTypes)
    {
        this.wantedArtifactTypes = createSetFromCommaSeparatedString(artifactTypes);
    }

    private HashSet<String> createSetFromCommaSeparatedString(final String commaSeparatedString)
    {
        return new HashSet<>(Arrays.asList(commaSeparatedString.split(",\\s*")));
    }

    /**
     * Set a list of artifact types to be applied as filter during import
     * 
     * @param artifactTypes
     *            list of wanted artifact types
     */
    public void setA(final String artifactTypes)
    {
        setWantedArtifactTypes(artifactTypes);
    }

    /**
     * Get a list of tags to be applied as filter during import
     * 
     * @return list of wanted tags
     */
    public Set<String> getWantedTags()
    {
        return this.wantedTags;
    }

    /**
     * Set a list of tags to be applied as filter during import
     * 
     * @param tags
     *            list of wanted tags
     */
    public void setWantedTags(final String tags)
    {
        this.wantedTags = createSetFromCommaSeparatedString(tags);
    }

    /**
     * Set a list of tags to be applied as filter during import
     * 
     * @param tags
     *            list of wanted tags
     */
    public void setT(final String tags)
    {
        setWantedTags(tags);
    }
}
