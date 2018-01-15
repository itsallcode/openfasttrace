package openfasttrack.importer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Super class for factories producing {@link Importer}s.
 */
public abstract class ImporterFactory
{
    /**
     * Returns <code>true</code> if this {@link ImporterFactory} supports
     * importing the given file based on its file extension.
     *
     * @param file
     *            the file to check.
     * @return <code>true</code> if the given file is supported for importing.
     */
    public abstract boolean supportsFile(final Path file);

    /**
     * Create an importer that is able to read the given file.
     *
     * @param file
     *            the file from which specification items are imported
     * @param charset
     *            the charset used for importing
     * @param listener
     *            the listener to be informed about detected specification item
     *            fragments
     * @return an importer instance
     */
    public abstract Importer createImporter(final Path file, final Charset charset,
            final ImportEventListener listener);

    protected static BufferedReader createReader(final Path file, final Charset charset)
    {
        try
        {
            return Files.newBufferedReader(file, charset);
        }
        catch (final IOException e)
        {
            throw new ImporterException("Error reading file '" + file + "': " + e.getMessage(), e);
        }
    }
}
