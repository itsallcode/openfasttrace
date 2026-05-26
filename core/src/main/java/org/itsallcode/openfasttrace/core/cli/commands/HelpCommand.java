package org.itsallcode.openfasttrace.core.cli.commands;

import org.itsallcode.openfasttrace.core.VersionProvider;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Handler for printing command line usage.
 *
 * impl~cli.help~1
 */
public class HelpCommand implements Performable
{
    /** The command line action for running this command. */
    public static final String COMMAND_NAME = "help";
    /** Whether the OFT command was used correctly. */
    private final boolean validUsage;

    /**
     * Create a new {@link HelpCommand}.
     *
     * @param validUsage whether the OFT command was used correctly
     */
    public HelpCommand(final boolean validUsage)
    {
        this.validUsage = validUsage;
    }

    @Override
    @SuppressWarnings("java:S106") // Using System.out by intention
    public boolean run()
    {
        final String version = new VersionProvider().getVersion();
        final String usage = loadResource("/usage.txt").replace("${version}", version);
        System.out.println(usage);
        return validUsage;
    }

    private String loadResource(final String resourceName)
    {
        try (InputStream stream = getResource(resourceName).openStream())
        {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(
                    "Unable to load CLI usage from resource file. This is a software bug. Please report.", exception);
        }
    }

    private URL getResource(final String resourceName)
    {
        final URL url = this.getClass().getResource(resourceName);
        if (url == null)
        {
            throw new IllegalStateException("Unable to locate CLI usage text resource '" + resourceName
                    + "'. This is a software bug. Please report.");
        }
        return url;
    }
}
