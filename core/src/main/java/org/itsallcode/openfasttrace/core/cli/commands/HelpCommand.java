package org.itsallcode.openfasttrace.core.cli.commands;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Handler for printing command line usage.
 */
public class HelpCommand implements Performable
{
    /** The command line action for running this command. */
    public static final String COMMAND_NAME = "help";

    @Override
    @SuppressWarnings("java:S106") // Using System.out by intention
    public boolean run()
    {
        final String usage = loadResource("/usage.txt");
        System.out.println(usage);
        return true;
    }

    private String loadResource(String resourceName)
    {
        try (InputStream stream = getResource(resourceName).openStream())
        {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        catch (IOException exception)
        {
            throw new UncheckedIOException("Unable to load CLI usage from resource file. This is a software bug. Please report.", exception);
        }
    }

    private URL getResource(String resourceName)
    {
        final URL url = this.getClass().getResource(resourceName);
        if (url == null)
        {
            throw new IllegalStateException("Unable to locate CLI usage text resource '" + resourceName + "'. This is a software bug. Please report.");
        }
        return url;
    }

}
