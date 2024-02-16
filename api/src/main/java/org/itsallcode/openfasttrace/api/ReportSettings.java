package org.itsallcode.openfasttrace.api;

import java.util.Objects;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.api.report.ReportConstants;
import org.itsallcode.openfasttrace.api.report.ReportVerbosity;

/**
 * This class implements a parameter object to control the settings of OFT's
 * report mode.
 */
public class ReportSettings
{
    private final ReportVerbosity verbosity;
    private final boolean showOrigin;
    private final String outputFormat;
    private final Newline newline;
    private final ColorScheme colorScheme;
    private final DetailsSectionDisplay detailsSectionDisplay;

    private ReportSettings(final Builder builder)
    {
        this.verbosity = builder.verbosity;
        this.showOrigin = builder.showOrigin;
        this.outputFormat = builder.outputFormat;
        this.newline = builder.newline;
        this.colorScheme = Objects.requireNonNull(builder.colorScheme, "colorScheme");
        this.detailsSectionDisplay = Objects.requireNonNull(builder.detailsSectionDisplay, "detailsSectionDisplay");
    }

    /**
     * Get the report verbosity
     * 
     * @return report verbosity
     */
    public ReportVerbosity getReportVerbosity()
    {
        return this.verbosity;
    }

    /**
     * Should the origin of a specification item be shown in the report?
     * 
     * @return {@code true} if the origin should be shown
     */
    public boolean showOrigin()
    {
        return this.showOrigin;
    }

    /**
     * Get the report output format (e.g. "plain" or "html")
     * 
     * @return report output format
     */
    public String getOutputFormat()
    {
        return this.outputFormat;
    }

    /**
     * Get the newline format
     * 
     * @return newline format
     */
    public Newline getNewline()
    {
        return this.newline;
    }

    /**
     * Get the color scheme
     *
     * @return color scheme
     */
    public ColorScheme getColorScheme()
    {
        return this.colorScheme;
    }

    /**
     * Get the details section display status.
     * 
     * @return display status
     */
    public DetailsSectionDisplay getDetailsSectionDisplay()
    {
        return detailsSectionDisplay;
    }

    /**
     * Create default report settings
     * 
     * @return default settings
     */
    public static ReportSettings createDefault()
    {
        return builder().build();
    }

    /**
     * Create a builder for {@link ReportSettings}
     * 
     * @return builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for {@link ReportSettings}
     */
    public static class Builder
    {
        private DetailsSectionDisplay detailsSectionDisplay = DetailsSectionDisplay.COLLAPSE;
        private Newline newline = Newline.UNIX;
        private String outputFormat = ReportConstants.DEFAULT_REPORT_FORMAT;
        private boolean showOrigin = false;
        private ReportVerbosity verbosity = ReportVerbosity.FAILURE_DETAILS;
        private ColorScheme colorScheme = ColorScheme.BLACK_AND_WHITE;

        private Builder()
        {
            // empty by intention
        }

        /**
         * Create a new instance of {@link ReportSettings}
         * 
         * @return report settings
         */
        public ReportSettings build()
        {
            return new ReportSettings(this);
        }

        /**
         * Set the report verbosity. Default:
         * {@link ReportVerbosity#FAILURE_DETAILS}.
         * 
         * @param verbosity
         *            report verbosity
         * @return {@code this} for fluent programming
         */
        public Builder verbosity(final ReportVerbosity verbosity)
        {
            this.verbosity = verbosity;
            return this;
        }

        /**
         * Set the whether the origin of specification items should be shown in
         * the report. Default: {@code false}.
         * 
         * @param showOrigin
         *            set to {@code true} if the origin should be shown
         * @return {@code this} for fluent programming
         */
        public Builder showOrigin(final boolean showOrigin)
        {
            this.showOrigin = showOrigin;
            return this;
        }

        /**
         * Set the output format. Default:
         * {@link ReportConstants#DEFAULT_REPORT_FORMAT}.
         * 
         * @param outputFormat
         *            output format
         * @return {@code this} for fluent programming
         */
        public Builder outputFormat(final String outputFormat)
        {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Set the newline format. Default: {@link Newline#UNIX}.
         * 
         * @param newline
         *            newline format
         * @return {@code this} for fluent programming
         */
        public Builder newline(final Newline newline)
        {
            this.newline = newline;
            return this;
        }

        /**
         * Set the desired color scheme. Default:
         * {@link ColorScheme#BLACK_AND_WHITE}.
         *
         * @param colorScheme
         *            color scheme to use
         * @return {@code this} for fluent programming
         */
        public Builder colorScheme(final ColorScheme colorScheme)
        {
            this.colorScheme = Objects.requireNonNull(colorScheme, "colorScheme");
            return this;
        }

        /**
         * Set the desired details section display status. Default:
         * {@link DetailsSectionDisplay#COLLAPSE}.
         *
         * @param detailsSectionDisplay
         *            display status to use
         * @return {@code this} for fluent programming
         */
        public Builder detailsSectionDisplay(final DetailsSectionDisplay detailsSectionDisplay)
        {
            this.detailsSectionDisplay = Objects.requireNonNull(detailsSectionDisplay, "detailsSectionDisplay");
            return this;
        }
    }
}
