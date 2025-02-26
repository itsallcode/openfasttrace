package org.itsallcode.openfasttrace.report.ux.model;

import org.itsallcode.openfasttrace.api.ReportSettings;

/**
 * Extended settings for the reporter
 */
public class UxReporterSettings extends ReportSettings
{
    /**
     * @param builder creates the settings
     */
    public UxReporterSettings(final Builder builder)
    {
        super(builder);
    }

    /**
     * Builds the setting
     */
    public static class Builder extends ReportSettings.Builder {
        /**
         * New builder
         */
        public Builder()
        {
            super();
        }

        /**
         * @return the settings
         */
        @Override public UxReporterSettings build()
        {
            return new UxReporterSettings(this);
        }
    }
}
