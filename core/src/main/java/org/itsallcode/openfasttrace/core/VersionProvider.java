package org.itsallcode.openfasttrace.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Provides the version of OpenFastTrace from a resource file generated during
 * build.
 */
public class VersionProvider
{
    private static final Logger LOGGER = Logger.getLogger(VersionProvider.class.getName());
    private static final String VERSION_PROPERTIES = "/version.properties";
    private static final String UNKNOWN_VERSION = "unknown";

    /**
     * Create a new instance of the {@link VersionProvider}.
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
        final URL resource = getClass().getResource(VERSION_PROPERTIES);
        if (resource == null)
        {
            return UNKNOWN_VERSION;
        }
        try (InputStream stream = resource.openStream())
        {
            final Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty("version", UNKNOWN_VERSION);
        }
        catch (final IOException exception)
        {
            LOGGER.warning("Error loading version from resource file: " + exception.getMessage());
            return UNKNOWN_VERSION;
        }
    }
}
