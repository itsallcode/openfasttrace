package org.itsallcode.openfasttrace.api.importer;

import java.util.Objects;

import org.itsallcode.openfasttrace.api.core.serviceloader.Initializable;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * Super class for factories producing {@link Importer}s.
 */
public abstract class ImporterFactory implements Initializable<ImporterContext>
{
    private ImporterContext context;

    /**
     * Create a new {@link ImporterFactory}.
     */
    protected ImporterFactory()
    {
        // empty by intention
    }

    /**
     * Returns {@code true} if this {@link ImporterFactory} supports
     * importing the given file based on its file extension.
     *
     * @param file
     *            the file to check.
     * @return {@code true} if the given file is supported for importing.
     */
    public abstract boolean supportsFile(final InputFile file);

    /**
     * Create an importer that is able to read the given file.
     *
     * @param file
     *            the file from which specification items are imported
     * @param listener
     *            the listener to be informed about detected specification item
     *            fragments
     * @return an importer instance
     */
    public abstract Importer createImporter(final InputFile file,
            final ImportEventListener listener);

    @Override
    public void init(final ImporterContext context)
    {
        this.context = context;
    }

    /**
     * Get the {@link ImporterContext} set by the {@link #init(ImporterContext)}
     * method.
     * 
     * @return the {@link ImporterContext}.
     */
    public ImporterContext getContext()
    {
        return Objects.requireNonNull(this.context, "Context was not initialized");
    }
}
