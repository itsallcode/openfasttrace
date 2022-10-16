package org.itsallcode.openfasttrace.core.cli;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;

/**
 * This class allows users to query the current directory.
 */
public class StandardDirectoryService implements DirectoryService
{
    /**
     * Create a new {@link StandardDirectoryService}.
     */
    public StandardDirectoryService()
    {
        // empty by intention
    }

    @Override
    public String getCurrent()
    {
        return System.getProperty("user.dir");
    }
}