package org.itsallcode.openfasttrace.api.report;

/*-
 * #%L
 * OpenFastTrace API
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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
