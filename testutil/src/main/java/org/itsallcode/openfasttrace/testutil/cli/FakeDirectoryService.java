package org.itsallcode.openfasttrace.testutil.cli;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;

public class FakeDirectoryService implements DirectoryService
{
    private final String fakeCurrentDir;

    public FakeDirectoryService(final String fakeCurrentDir)
    {
        this.fakeCurrentDir = fakeCurrentDir;
    }

    @Override
    public String getCurrent()
    {
        return this.fakeCurrentDir;
    }
}
