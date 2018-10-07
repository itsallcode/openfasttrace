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
import org.itsallcode.openfasttrace.exporter.ExporterConstants;

/**
 * This class implements a parameter object to control the settings of OFT's
 * export mode.
 */
public class ExportSettings
{
    private final String outputFormat;
    private final Newline newline;

    private ExportSettings(final Builder builder)
    {
        this.outputFormat = builder.outputFormat;
        this.newline = builder.newline;
    }

    /**
     * Get the conversion output format
     * 
     * @return output format
     */
    public String getOutputFormat()
    {
        return this.outputFormat;
    }

    /**
     * Get the newline format used in the conversion results
     * 
     * @return newline format
     */
    public Newline getNewline()
    {
        return this.newline;
    }

    /**
     * Builder for {@link ExportSettings}
     */
    public static class Builder
    {
        private String outputFormat = ExporterConstants.DEFAULT_OUTPUT_FORMAT;
        private Newline newline = Newline.UNIX;

        /**
         * Set the converter output format
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
         * Get the newline format to be used in the conversion result
         * 
         * @param newline
         *            newline format
         * @return <code>this</code> for fluent programming
         */
        public Builder newline(final Newline newline)
        {
            this.newline = newline;
            return this;
        }

        /**
         * Create a new instance of {@link ExportSettings}
         * 
         * @return new instance
         */
        public ExportSettings build()
        {
            return new ExportSettings(this);
        }
    }
}
