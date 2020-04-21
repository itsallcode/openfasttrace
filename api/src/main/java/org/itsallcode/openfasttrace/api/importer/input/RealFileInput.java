package org.itsallcode.openfasttrace.api.importer.input;

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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An {@link InputFile} for a file on disk, represented by a {@link Path}.
 */
public class RealFileInput implements InputFile
{
    private final Path path;
    private final Charset charset;

    private RealFileInput(final Path path, final Charset charset)
    {
        this.path = path;
        this.charset = charset;
    }

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
     * @param path
     *            path to file on disk.
     * @param charset
     *            the {@link Charset} used when reading this file.
     * @return an {@link InputFile}.
     */
    public static InputFile forPath(final Path path, final Charset charset)
    {
        return new RealFileInput(path, charset);
    }

    @Override
    public BufferedReader createReader() throws IOException
    {
        return Files.newBufferedReader(this.path, this.charset);
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
        return true;
    }

    @Override
    public Path toPath()
    {
        return this.path;
    }
}
