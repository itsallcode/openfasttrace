package org.itsallcode.openfasttrace.testutil.importer.input;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Path;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;

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
     *            the file content.
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
     *            a dummy path.
     * @param reader
     *            the base reader.
     * @return an {@link InputFile}.
     */
    public static InputFile forReader(final Path path, final BufferedReader reader)
    {
        return new StreamInput(path, reader);
    }

    @Override
    public BufferedReader createReader()
    {
        return this.reader;
    }

    @Override
    public String getPath()
    {
        return this.path.toString();
    }

    @Override
    public String toString()
    {
        return getPath();
    }

    @Override
    public boolean isRealFile()
    {
        return false;
    }

    @Override
    public Path toPath()
    {
        throw new UnsupportedOperationException("toPath() not supported for StreamInput");
    }
}
