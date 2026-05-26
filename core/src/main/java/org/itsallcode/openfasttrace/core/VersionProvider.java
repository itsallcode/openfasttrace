package org.itsallcode.openfasttrace.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Provides the version of OpenFastTrace from a resource file generated during
 * build.
 */
public class VersionProvider
{

    private static final String VERSION_PROPERTIES = "/version.properties";
    private static final String UNKNOWN_VERSION = "unknown";

    /**
     * Default constructor.
     */
    public VersionProvider()
    {
        // Default constructor
    }

    /**
     * Loads the version number from the version.properties resource.
     *
     * @return the version string or "unknown" if it cannot be loaded.
     */
    // [impl->dsn~cli.version~1]
    public String getVersion()
    {
        final Properties properties = new Properties();
        final URL resource = getClass().getResource(VERSION_PROPERTIES);
        if (resource == null)
        {
            return UNKNOWN_VERSION;
        }
        try (InputStream stream = resource.openStream())
        {
            properties.load(stream);
            return properties.getProperty("version", UNKNOWN_VERSION);
        }
        catch (final IOException exception)
        {
            return UNKNOWN_VERSION;
        }
    }
}
