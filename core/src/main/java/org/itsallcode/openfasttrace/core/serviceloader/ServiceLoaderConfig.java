package org.itsallcode.openfasttrace.core.serviceloader;

import java.nio.file.Path;
import java.util.*;

/**
 * Configuration for the service loader.
 * <p>
 * This object collects all settings that control how OFT discovers and loads
 * services (importers, exporters, and reporters). It currently holds the plugin
 * directory, whether the current class path is searched, and the plugins
 * configured at runtime. Additional options can be added here in the future
 * without changing the service loader API.
 */
public final class ServiceLoaderConfig
{
    private final Path pluginsDirectory;
    private final boolean searchCurrentClasspath;
    private final List<Plugin> plugins;

    private ServiceLoaderConfig(final Builder builder)
    {
        this.pluginsDirectory = builder.pluginsDirectory;
        this.searchCurrentClasspath = builder.searchCurrentClasspath;
        this.plugins = Collections.unmodifiableList(new ArrayList<>(builder.plugins));
    }

    /**
     * Create the default configuration.
     * <p>
     * The default configuration searches the current class path and the default
     * plugin directory in the user's home and configures no additional plugins.
     * 
     * @return the default configuration
     */
    public static ServiceLoaderConfig createDefault()
    {
        return builder().build();
    }

    /**
     * Create a builder for {@link ServiceLoaderConfig}.
     * 
     * @return a new builder
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Create a builder initialized with the values of this configuration.
     * 
     * @return a new builder
     */
    public Builder toBuilder()
    {
        return new Builder()
                .pluginsDirectory(this.pluginsDirectory)
                .searchCurrentClasspath(this.searchCurrentClasspath)
                .addPlugins(this.plugins);
    }

    /**
     * Get the directory that is searched for plugins.
     * 
     * @return the plugin directory
     */
    public Path getPluginsDirectory()
    {
        return this.pluginsDirectory;
    }

    /**
     * Check whether the current class path is searched for services.
     * 
     * @return {@code true} if the current class path is searched
     */
    public boolean isSearchCurrentClasspath()
    {
        return this.searchCurrentClasspath;
    }

    /**
     * Get the plugins configured at runtime.
     * 
     * @return the configured plugins
     */
    public List<Plugin> getPlugins()
    {
        return this.plugins;
    }

    /**
     * Builder for {@link ServiceLoaderConfig}.
     */
    public static final class Builder
    {
        private Path pluginsDirectory = defaultPluginsDirectory();
        private boolean searchCurrentClasspath = true;
        private final List<Plugin> plugins = new ArrayList<>();

        private Builder()
        {
            // Prevent instantiation from outside this class.
        }

        private static Path defaultPluginsDirectory()
        {
            return Path.of(System.getProperty("user.home")).resolve(".oft").resolve("plugins");
        }

        /**
         * Set the directory that is searched for plugins. Default: the
         * {@code .oft/plugins} directory in the user's home.
         * 
         * @param pluginsDirectory
         *            the plugin directory
         * @return {@code this} for fluent programming
         */
        public Builder pluginsDirectory(final Path pluginsDirectory)
        {
            this.pluginsDirectory = Objects.requireNonNull(pluginsDirectory, "pluginsDirectory");
            return this;
        }

        /**
         * Set whether the current class path is searched for services. Default:
         * {@code true}.
         * 
         * @param searchCurrentClasspath
         *            {@code true} if the current class path should be searched
         * @return {@code this} for fluent programming
         */
        public Builder searchCurrentClasspath(final boolean searchCurrentClasspath)
        {
            this.searchCurrentClasspath = searchCurrentClasspath;
            return this;
        }

        /**
         * Add a plugin configured at runtime.
         * 
         * @param plugin
         *            the plugin to add
         * @return {@code this} for fluent programming
         */
        public Builder addPlugin(final Plugin plugin)
        {
            this.plugins.add(Objects.requireNonNull(plugin, "plugin"));
            return this;
        }

        /**
         * Add plugins configured at runtime.
         * 
         * @param plugins
         *            the plugins to add
         * @return {@code this} for fluent programming
         */
        public Builder addPlugins(final List<Plugin> plugins)
        {
            this.plugins.addAll(Objects.requireNonNull(plugins, "plugins"));
            return this;
        }

        /**
         * Create a new {@link ServiceLoaderConfig}.
         * 
         * @return a new configuration
         */
        public ServiceLoaderConfig build()
        {
            return new ServiceLoaderConfig(this);
        }
    }
}
