package openfasttrack.importer;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Super class for factories producing {@link Importer}s.
 */
public abstract class ImporterFactory
{
    private final static Logger LOG = Logger.getLogger(ImporterFactory.class.getName());
    private final Set<Pattern> supportedFilenamePatterns;

    protected ImporterFactory(final String... supportedFilenamePatterns)
    {
        this(asList(supportedFilenamePatterns));
    }

    protected ImporterFactory(final Collection<String> supportedFileExtensions)
    {
        this.supportedFilenamePatterns = supportedFileExtensions.stream() //
                .map(Pattern::compile) //
                .collect(toSet());
    }

    /**
     * Returns <code>true</code> if this {@link ImporterFactory} supports
     * importing the given file based on its file extension.
     *
     * @param file
     *            the file to check.
     * @return <code>true</code> if the given file is supported for importing.
     */
    public boolean supportsFile(final Path file)
    {
        final String fileName = file.getFileName().toString();
        for (final Pattern pattern : this.supportedFilenamePatterns)
        {
            if (pattern.matcher(fileName).matches())
            {
                LOG.finest(() -> "Filename '" + fileName + "' matches '" + pattern
                        + "': supported  by " + this.getClass().getName());
                return true;
            }
        }
        LOG.finest(() -> "Filename '" + fileName + "' does not match any regexp of "
                + this.supportedFilenamePatterns + ": not supported by "
                + this.getClass().getName());
        return false;
    }

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
    public Importer createImporter(final Path file, final Charset charset,
            final ImportEventListener listener)
    {
        if (!supportsFile(file))
        {
            throw new ImporterException("File '" + file + "' not supported for import");
        }
        LOG.finest(() -> "Creating importer for file " + file);
        final BufferedReader reader = createReader(file, charset);
        return createImporter(reader, listener);
    }

    private BufferedReader createReader(final Path file, final Charset charset)
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

    /**
     * Create an importer that is able to read the given file
     *
     * @param reader
     *            the reader from which specification items are imported
     * @param listener
     *            the listener to be informed about detected specification item
     *            fragments
     * @return an importer instance
     */
    public abstract Importer createImporter(final Reader reader,
            final ImportEventListener listener);
}