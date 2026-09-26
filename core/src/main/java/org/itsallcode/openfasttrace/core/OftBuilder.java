package org.itsallcode.openfasttrace.core;

import java.nio.file.Path;

import org.itsallcode.openfasttrace.core.serviceloader.Plugin;
import org.itsallcode.openfasttrace.core.serviceloader.ServiceLoaderConfig;

/**
 * Builder for creating {@link Oft} instances with custom configuration.
 * <p>
 * Use this builder to configure additional plugins that OFT should load at
 * runtime in addition to the plugins discovered from the default locations.
 */
public final class OftBuilder
{
    private final ServiceLoaderConfig.Builder serviceLoaderConfig = ServiceLoaderConfig.builder();

    OftBuilder()
    {
        // Prevent instantiation from outside this package.
    }

    /**
     * Add a named plugin consisting of one or more JAR files.
     * <p>
     * All JAR files passed in one call are loaded through the same separate
     * ClassLoader, matching the semantics of a plugin directory under
     * {@code $HOME/.oft/plugins/<plugin-name>/}. The JAR files may contain one
     * or more service providers; OFT loads all of them.
     * 
     * @param name
     *            name of the plugin, used for the ClassLoader name and logging
     * @param jars
     *            paths to the JAR files that make up the plugin
     * @return {@code this} for fluent programming
     * @throws IllegalArgumentException
     *             if the name is {@code null} or blank, if no JAR is given, or
     *             if a path is {@code null} or does not point to an existing JAR
     *             file
     */
    public OftBuilder addPlugin(final String name, final Path... jars)
    {
        this.serviceLoaderConfig.addPlugin(Plugin.of(name, jars));
        return this;
    }

    /**
     * Create a new {@link Oft} instance with the configured plugins.
     * 
     * @return a new {@link Oft} instance
     */
    public Oft build()
    {
        return new OftRunner(new ServiceFactory(this.serviceLoaderConfig.build()));
    }
}
