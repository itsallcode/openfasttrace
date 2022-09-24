package org.itsallcode.openfasttrace.core;

import org.itsallcode.openfasttrace.api.core.Newline;
import org.itsallcode.openfasttrace.core.exporter.ExporterConstants;

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
     * Create default exporter settings
     * 
     * @return default exporter settings
     */
    public static ExportSettings createDefault()
    {
        return builder().build();
    }

    /**
     * Create an export settings builder
     * 
     * @return export settings builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for {@link ExportSettings}
     */
    public static class Builder
    {
        private String outputFormat = ExporterConstants.DEFAULT_OUTPUT_FORMAT;
        private Newline newline = Newline.UNIX;

        private Builder()
        {
        }

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
