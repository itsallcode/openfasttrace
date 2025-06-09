package org.itsallcode.openfasttrace.testutil.importer.input;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;

/**
 * An implementation of {@link InputFile} that reads from a
 * {@link BufferedReader}. This is useful for tests to avoid using real files.
 */
public class StreamInput implements InputFile
{
    private final Path path;
    private final BufferedReader reader;

    private StreamInput(final Path path, final BufferedReader reader)
    {
        this.path = path;
        this.reader = reader;
    }

    /**
     * Create an {@link InputFile} for a given file content. This is useful for
     * tests to avoid using real files.
     * 
     * @param path
     *            a dummy path.
     * @param content
     *            file content.
     * @return an {@link InputFile}.
     */
    public static InputFile forContent(final Path path, final String content)
    {
        return forReader(path, new BufferedReader(new StringReader(content)));
    }

    /**
     * Create an {@link InputFile} for a {@link BufferedReader}. This is useful
     * for tests to avoid using real files.
     * 
     * @param path
     *            a dummy path
     * @param reader
     *            base reader
     * @return an {@link InputFile}.
     */
    public static InputFile forReader(final Path path, final BufferedReader reader)
    {
        return new StreamInput(path, reader);
    }

    /**
     * Creates a reader for the input file.
     * 
     * @return the reader for the input file
     */
    @Override
    public BufferedReader createReader()
    {
        return this.reader;
    }

    /**
     * Gets the path of the input file.
     * 
     * @return the path of the input file as a string
     */
    @Override
    public String getPath()
    {
        return this.path.toString();
    }

    /**
     * Returns a string representation of this input file.
     * 
     * @return the path of the input file
     */
    @Override
    public String toString()
    {
        return getPath();
    }

    /**
     * Checks if this input file is a real file on the file system.
     * 
     * @return {@code false} since this is a stream-based input
     */
    @Override
    public boolean isRealFile()
    {
        return false;
    }

    /**
     * Converts this input file to a {@link Path}.
     * 
     * @return never returns
     * @throws UnsupportedOperationException
     *             always thrown since this operation is not supported for
     *             stream-based input
     */
    @Override
    public Path toPath()
    {
        throw new UnsupportedOperationException("toPath() not supported for StreamInput");
    }
}
