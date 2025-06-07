package org.itsallcode.openfasttrace.testutil.cli;

import org.itsallcode.openfasttrace.api.cli.DirectoryService;

/**
 * A fake implementation of {@link DirectoryService} that returns a predefined
 * directory path.
 * <p>
 * Normally, the directory service provides the current directory path. This
 * fake implementation can be used in tests to inject a predefined directory
 * path.
 * </p>
 */
public class FakeDirectoryService implements DirectoryService
{
    private final String fakeCurrentDir;

    /**
     * Create a new {@link FakeDirectoryService} that returns a predefined
     * directory path.
     * 
     * @param fakeCurrentDir
     *            the directory path to be returned by {@link #getCurrent()}
     */
    public FakeDirectoryService(final String fakeCurrentDir)
    {
        this.fakeCurrentDir = fakeCurrentDir;
    }

    /**
     * Get the predefined current directory path.
     * 
     * @return the predefined directory path
     */
    @Override
    public String getCurrent()
    {
        return this.fakeCurrentDir;
    }
}
