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
import java.io.StringReader;
import java.nio.file.Path;

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
