package org.itsallcode.openfasttrace.api.importer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.itsallcode.openfasttrace.api.FilterSettings;
import org.itsallcode.openfasttrace.api.importer.tag.config.PathConfig;

/**
 * Configuration for an {@link Importer}.
 */
public class ImportSettings
{
    private final List<Path> inputs;
    private final FilterSettings filter;
    private final List<PathConfig> pathConfigs;

    private ImportSettings(final Builder builder)
    {
        this.inputs = builder.inputs;
        this.filter = builder.filter;
        this.pathConfigs = builder.pathConfigs;
    }

    /**
     * Get the list of paths to be scanned for importable artifacts
     * 
     * @return list of input paths
     */
    public List<Path> getInputs()
    {
        return this.inputs;
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
     * Create an import settings builder
     * 
     * @return import settings builder
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
        private final List<Path> inputs = new ArrayList<>();
        private FilterSettings filter = FilterSettings.createAllowingEverything();
        private List<PathConfig> pathConfigs = new ArrayList<>();

        private Builder()
        {
        }

        /**
         * Add a list of input paths to be scanned for importable artifacts
         * 
         * @param inputs
         *            input paths to be scanned
         * @return <code>this</code> for fluent programming
         */
        public Builder addInputs(final List<Path> inputs)
        {
            this.inputs.addAll(inputs);
            return this;
        }

        /**
         * Add one or more input paths to be scanned for importable artifacts
         * 
         * @param inputs
         *            input paths to be scanned
         * @return <code>this</code> for fluent programming
         */
        public Builder addInputs(final Path... inputs)
        {
            for (final Path input : inputs)
            {
                this.inputs.add(input);
            }
            return this;
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