package org.itsallcode.openfasttrace.importer.input;

/*-
 * #%L
 * OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2018 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This represents a file (either physical or virtual as a stream) that can be
 * read by an importer.
 */
public interface InputFile
{
    /**
     * Create an {@link InputFile} for a real file on disk.
     * {@link StandardCharsets#UTF_8} is used for reading the file.
     * 
     * @param file
     *            a real file on disk.
     * @return an {@link InputFile}.
     */
    public static InputFile forPath(final Path file)
    {
        return forPath(file, StandardCharsets.UTF_8);
    }

    /**
     * Create an {@link InputFile} for a real file on disk.
     * 
     * @param file
     *            a real file on disk.
     * @param charset
     *            the {@link Charset} used when reading this file.
     * @return an {@link InputFile}.
     */
    public static InputFile forPath(final Path path, final Charset charset)
    {
        return new RealFileInput(path, charset);
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

    /**
     * Create an {@link InputFile} for a {@link ZipEntry} in a {@link ZipFile}.
     * {@link StandardCharsets#UTF_8} is used for reading the file.
     * 
     * @param zip
     *            the {@link ZipFile} containing the entry.
     * @param entry
     *            the {@link ZipEntry} to read.
     * @return an {@link InputFile}.
     */
    public static InputFile forZipEntry(final ZipFile zip, final ZipEntry entry)
    {
        return forZipEntry(zip, entry, StandardCharsets.UTF_8);
    }

    /**
     * Create an {@link InputFile} for a {@link ZipEntry} in a {@link ZipFile}.
     * 
     * @param zip
     *            the {@link ZipFile} containing the entry.
     * @param entry
     *            the {@link ZipEntry} to read.
     * @param charset
     *            the {@link Charset} used for reading the entry.
     * @return an {@link InputFile}.
     */
    public static InputFile forZipEntry(final ZipFile zip, final ZipEntry entry,
            final Charset charset)
    {
        if (entry.isDirectory())
        {
            throw new IllegalArgumentException("ZIP entry " + entry + " must not be a directory");
        }
        return new ZipEntryInput(zip, entry, charset);
    }

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
