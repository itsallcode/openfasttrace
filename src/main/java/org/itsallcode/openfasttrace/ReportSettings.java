package org.itsallcode.openfasttrace;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
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

import org.itsallcode.openfasttrace.core.Newline;
import org.itsallcode.openfasttrace.report.ReportConstants;
import org.itsallcode.openfasttrace.report.ReportVerbosity;

/**
 * This class implements a parameter object to control the settings of OFT's
 * report mode.
 */
public class ReportSettings
{
    private final ReportVerbosity verbosity;
    private final boolean showOrigin;
    private final String outputFormat;
    private final Newline newlineFormat;

    private ReportSettings(final Builder builder)
    {
        this.verbosity = builder.verbosity;
        this.showOrigin = builder.showOrigin;
        this.outputFormat = builder.outputFormat;
        this.newlineFormat = builder.newlineFormat;
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
     * @return <code>true</code> if the origin should be shown
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
    public Newline getNewlineFormat()
    {
        return this.newlineFormat;
    }

    /**
     * Builder for {@link ReportSettings}
     */
    public static class Builder
    {
        public Newline newlineFormat = Newline.UNIX;
        public String outputFormat = ReportConstants.DEFAULT_REPORT_FORMAT;
        public boolean showOrigin = false;
        ReportVerbosity verbosity = ReportVerbosity.FAILURE_DETAILS;

        /**
         * Create a new instance of {@link ReportSettings}
         * 
         * @return
         */
        public ReportSettings build()
        {
            return new ReportSettings(this);
        }

        /**
         * Set the report verbosity
         * 
         * @param verbosity
         *            report verbosity
         * @return <code>this</code> for fluent programming
         */
        public Builder verbosity(final ReportVerbosity verbosity)
        {
            this.verbosity = verbosity;
            return this;
        }

        /**
         * Set the whether the origin of specification items should be shown in
         * the report
         * 
         * @param showOrign
         *            set to <code>true</code> if the origin should be shown
         * @return <code>this</code> for fluent programming
         */
        public Builder showOrigin(final boolean showOrigin)
        {
            this.showOrigin = showOrigin;
            return this;
        }

        /**
         * Set the output format
         * 
         * @param outputFormat
         *            output format
         * @return <code>this</code> for fluent programming
         */
        public Builder outputFormat(final String outputFormat)
        {
            this.outputFormat = outputFormat;
            return this;
        }

        /**
         * Set the newline format
         * 
         * @param newlineFormat
         *            newline format
         * @return <code>this</code> for fluent programming
         */
        public Builder newlineFormat(final Newline newlineFormat)
        {
            this.newlineFormat = newlineFormat;
            return this;
        }
    }
}
