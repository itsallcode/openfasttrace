package org.itsallcode.openfasttrace.api.importer.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This represents a file (either physical or virtual as a stream) that can be
 * read by an importer.
 */
public interface InputFile
{

    /**
     * Get a {@link BufferedReader} for reading the file.
     * 
     * @return a {@link BufferedReader} for reading the file.
     * @throws IOException
     *             when there is an error reading the file.
     */
    BufferedReader createReader() throws IOException;

    /**
     * Get a string representation of the path.
     * 
     * @return a string representation of the path.
     */
    String getPath();

    /**
     * Check if this {@link InputFile} is based on a real file on disk.
     * 
     * @return <code>true</code> if this {@link InputFile} is based on a real
     *         file.
     */
    boolean isRealFile();

    /**
     * Get the {@link Path} to the file when {@link #isRealFile()} is
     * <code>true</code>. Else throws an {@link UnsupportedOperationException}.
     * 
     * @return the {@link Path} to the file
     * @throws UnsupportedOperationException
     *             when this is not a real file.
     */
    Path toPath();
}
