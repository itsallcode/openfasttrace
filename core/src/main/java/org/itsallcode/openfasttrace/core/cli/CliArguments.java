package org.itsallcode.openfasttrace.core.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.itsallcode.openfasttrace.api.ColorScheme;
import org.itsallcode.openfasttrace.api.DetailsSectionDisplay;
import org.itsallcode.openfasttrace.api.cli.DirectoryService;
import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.report.ReportConstants;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;
import org.itsallcode.openfasttrace.core.cli.commands.ConvertCommand;
import org.itsallcode.openfasttrace.core.cli.commands.TraceCommand;
import org.itsallcode.openfasttrace.core.exporter.ExporterConstants;

/**
 * This class implements a parameter object into which the command line parser
 * injects the results of parsing the command line.
 */
public class CliArguments
{
    /** Filter in command line arguments matching items with no tags. */
    public static final String NO_TAGS_MARKER = "_";
    // [impl->dsn~cli.default-newline-format~1]
    private Newline newline = Newline.fromRepresentation(System.lineSeparator());
    private List<String> unnamedValues;
    private Path outputFile;
    private String outputFormat;
    private ReportVerbosity reportVerbosity;
    private Set<String> wantedArtifactTypes = Collections.emptySet();
    private Set<String> wantedTags = Collections.emptySet();

    // [impl->dsn~reporting.plain-text.specification-item-origin~1]]
    // [impl->dsn~reporting.plain-text.linked-specification-item-origin~1]
    // [impl->dsn~reporting.html.specification-item-origin~1]
    // [impl->dsn~reporting.html.linked-specification-item-origin~1]
    private boolean showOrigin;
    private final DirectoryService directoryService;
    private ColorScheme colorScheme;

    // [impl->dsn~reporting.html.details-folding~1]
    private DetailsSectionDisplay detailsSectionDisplay;

    /**
     * Create new {@link CliArguments}.
     * 
     * @param directoryService
     *            the directory service used for evaluating command line
     *            arguments.
     */
    public CliArguments(final DirectoryService directoryService)
    {
        this.directoryService = directoryService;
    }

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
    public Optional<String> getCommand()
    {
        if (this.unnamedValues == null || this.unnamedValues.isEmpty())
        {
            return Optional.empty();
        }
        return Optional.of(this.unnamedValues.get(0));
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
            return List.of(this.directoryService.getCurrent());
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
     * Get the output format for an export or stream
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
            final Optional<String> command = this.getCommand();
            if (command.isPresent() && command.get().equals(TraceCommand.COMMAND_NAME))
            {
                return ReportConstants.DEFAULT_REPORT_FORMAT;
            }
            else if (command.isPresent() && command.get().equals(ConvertCommand.COMMAND_NAME))
            {
                return ExporterConstants.DEFAULT_OUTPUT_FORMAT;
            }
            else
            {
                throw new IllegalStateException("Illegal command \"" + command
                        + "\" encountered trying to set default output format.");
            }
        }
        return this.outputFormat;
    }

    /**
     * Set the output format for an export or stream
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
     * Set the output format for an export or stream
     * 
     * @param outputFormat
     *            the output format
     */
    public void setO(final String outputFormat)
    {
        setOutputFormat(outputFormat);
    }

    /**
     * Get the stream verbosity of a stream
     * 
     * @return the stream verbosity of a stream
     */
    public ReportVerbosity getReportVerbosity()
    {
        return (null == this.reportVerbosity) ? ReportConstants.DEFAULT_REPORT_VERBOSITY
                : this.reportVerbosity;
    }

    /**
     * Set the stream verbosity for a stream
     * 
     * @param reportVerbosity
     *            the verbosity of the stream
     */
    public void setReportVerbosity(final ReportVerbosity reportVerbosity)
    {
        this.reportVerbosity = reportVerbosity;
    }

    /**
     * Set the stream verbosity for a stream
     * 
     * @param reportVerbosity
     *            the verbosity of the stream
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
     * Get the color scheme.
     * <p>
     * Defaults to {@link ColorScheme#COLOR}. The switch <code>-f</code> overrides this setting, so that the color
     * scheme is always {@link ColorScheme#BLACK_AND_WHITE}.
     * </p>
     *
     * @return the color scheme
     */
    public ColorScheme getColorScheme()
    {
        if (this.getOutputPath() == null) {
            return (this.colorScheme == null) ? ColorScheme.COLOR : this.colorScheme;
        }
        else {
            return ColorScheme.BLACK_AND_WHITE;
        }
    }

    /**
     * Get the default display status of HTML report details sections.
     * <p>
     * Defaults to {@link DetailsSectionDisplay#COLLAPSE}.
     * </p>
     * 
     * @return display status
     */
    public DetailsSectionDisplay getDetailsSectionDisplay()
    {
        return detailsSectionDisplay == null ? DetailsSectionDisplay.COLLAPSE : detailsSectionDisplay;
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

    /**
     * Check if origin information should be shown in reports.
     * 
     * @return {@code true} if origin information should be shown in
     *         reports.
     */
    public boolean getShowOrigin()
    {
        return this.showOrigin;
    }

    /**
     * Choose whether to show origin information in reports.
     * 
     * @param showOrigin
     *            {@code true} if origin information should be shown in
     *            reports
     */
    public void setShowOrigin(final boolean showOrigin)
    {
        this.showOrigin = showOrigin;
    }

    /**
     * Choose whether to show origin information in reports.
     * 
     * @param showOrigin
     *            {@code true} if origin information should be shown in
     *            reports
     */
    public void setS(final boolean showOrigin)
    {
        setShowOrigin(showOrigin);
    }

    /**
     * Choose the color scheme.
     *
     * @param colorScheme color scheme to use for console output
     */
    public void setColorScheme(final ColorScheme colorScheme)
    {
        this.colorScheme = colorScheme;
    }

    /**
     * Choose the color scheme.
     *
     * @param colorScheme color scheme to use for console output
     */
    public void setC(final ColorScheme colorScheme)
    {
        this.setColorScheme(colorScheme);
    }

    /**
     * Choose the details display status.
     *
     * @param detailsSectionDisplay
     *            display status
     */
    public void setDetailsSectionDisplay(final DetailsSectionDisplay detailsSectionDisplay)
    {
        this.detailsSectionDisplay = detailsSectionDisplay;
    }
}
