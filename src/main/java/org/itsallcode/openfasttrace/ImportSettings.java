package org.itsallcode.openfasttrace;

import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.importer.tag.config.PathConfig;

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

public class ImportSettings
{
    private final FilterSettings filter;
    private final List<PathConfig> pathConfigs;

    protected ImportSettings(final Builder builder)
    {
        this.filter = builder.filter;
        this.pathConfigs = builder.pathConfigs;
    }

    /**
     * Get the filter settings
     * 
     * @return filter settings
     */
    public FilterSettings getFilters()
    {
        return this.filter;
    }

    /**
     * Get path configurations. Those are per-path settings that define how the
     * importer should interpret the specification item tags it found.
     * 
     * @return path configurations
     */
    public List<PathConfig> getPathConfigs()
    {
        return this.pathConfigs;
    }

    /**
     * Create a the default import settings
     * 
     * @return default import settings
     */
    public static ImportSettings createDefault()
    {
        return builder().build();
    }

    /**
     * Create an export settings builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Builder for {@link ImportSettings}
     */
    public static class Builder
    {
        private FilterSettings filter = FilterSettings.createAllowingEverything();
        private List<PathConfig> pathConfigs = new ArrayList<>();

        private Builder()
        {
        }

        /**
         * Set the filters to be used during import
         * 
         * @param filter
         *            filter settings
         * @return <code>this</code> for fluent programming
         */
        public Builder filter(final FilterSettings filter)
        {
            this.filter = filter;
            return this;
        }

        /**
         * Set path configurations
         * 
         * @param pathConfigs
         *            per-path importer configurations
         * @return <code>this</code> for fluent programming
         */
        public Builder pathConfigs(final List<PathConfig> pathConfigs)
        {
            this.pathConfigs = pathConfigs;
            return this;
        }

        /**
         * Create a new instance of {@link ImportSettings}
         * 
         * @return import settings instance
         */
        public ImportSettings build()
        {
            return new ImportSettings(this);
        }
    }
}