package org.itsallcode.openfasttrace.core.serviceloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A plugin configured at runtime, consisting of a name and one or more JAR
 * files.
 * <p>
 * All JAR files of a plugin are loaded through a single separate ClassLoader.
 * The JAR files may contain one or more service providers; OFT loads all of
 * them.
 */
public final class Plugin
{
    private final String name;
    private final List<Path> jars;

    private Plugin(final String name, final List<Path> jars)
    {
        this.name = name;
        this.jars = jars;
    }

    /**
     * Create a new plugin.
     * 
     * @param name
     *            name of the plugin, used for the ClassLoader name and logging
     * @param jars
     *            paths to the JAR files that make up the plugin
     * @return a new plugin
     * @throws IllegalArgumentException
     *             if the name is {@code null} or blank, if no JAR is given, or
     *             if a path is {@code null} or does not point to an existing JAR
     *             file
     */
    public static Plugin of(final String name, final Path... jars)
    {
        if (name == null || name.isBlank())
        {
            throw new IllegalArgumentException("Plugin name must not be null or blank.");
        }
        if (jars == null || jars.length == 0)
        {
            throw new IllegalArgumentException("At least one plugin JAR must be given for plugin '" + name + "'.");
        }
        final List<Path> validatedJars = new ArrayList<>(jars.length);
        for (final Path jar : jars)
        {
            validatedJars.add(validateJar(name, jar));
        }
        return new Plugin(name, Collections.unmodifiableList(validatedJars));
    }

    private static Path validateJar(final String name, final Path jar)
    {
        if (jar == null)
        {
            throw new IllegalArgumentException("Plugin JAR path must not be null for plugin '" + name + "'.");
        }
        if (!Files.isRegularFile(jar))
        {
            throw new IllegalArgumentException(
                    "Plugin JAR '" + jar + "' of plugin '" + name + "' does not exist or is not a file.");
        }
        if (!jar.getFileName().toString().toLowerCase(Locale.ENGLISH).endsWith(".jar"))
        {
            throw new IllegalArgumentException(
                    "Plugin file '" + jar + "' of plugin '" + name + "' is not a JAR file.");
        }
        return jar;
    }

    /**
     * Get the name of the plugin.
     * 
     * @return the name of the plugin
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the JAR files that make up the plugin.
     * 
     * @return the JAR files that make up the plugin
     */
    public List<Path> getJars()
    {
        return List.copyOf(this.jars);
    }

    @Override
    public String toString()
    {
        return "Plugin [name=" + this.name + ", jars=" + this.jars + "]";
    }
}
