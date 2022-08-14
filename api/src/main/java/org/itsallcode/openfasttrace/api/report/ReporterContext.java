package org.itsallcode.openfasttrace.api.report;

import org.itsallcode.openfasttrace.api.ReportSettings;

/**
 * Common context shared by all {@link ReporterFactory}s. This allows reporters
 * to access common infrastructure, e.g. the {@link ReportSettings}.
 */
public class ReporterContext
{
    private final ReportSettings settings;

    /**
     * Create a new {@link ReporterContext} with the given
     * {@link ReportSettings}.
     * 
     * @param settings
     *            the settings for the new context.
     */
    public ReporterContext(ReportSettings settings)
    {
        this.settings = settings;
    }

    /**
     * Get the settings for this context.
     * 
     * @return the settings for this context.
     */
    public ReportSettings getSettings()
    {
        return settings;
    }
}
