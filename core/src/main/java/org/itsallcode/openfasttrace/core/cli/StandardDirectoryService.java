package org.itsallcode.openfasttrace.core.cli;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;

/**
 * This class allows users to query the current directory.
 */
public class StandardDirectoryService implements DirectoryService
{
    @Override
    public String getCurrent()
    {
        return System.getProperty("user.dir");
    }
}