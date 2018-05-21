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

public interface InputFile
{
    public static InputFile forPath(final Path file)
    {
        return forPath(file, StandardCharsets.UTF_8);
    }

    public static InputFile forPath(final Path path, final Charset charset)
    {
        return new RealFileInput(path, charset);
    }

    public static InputFile forReader(final Path path, final BufferedReader reader)
    {
        return new StreamInput(path, reader);
    }

    public static InputFile forZipEntry(final ZipFile zip, final ZipEntry entry)
    {
        return forZipEntry(zip, entry, StandardCharsets.UTF_8);
    }

    public static InputFile forZipEntry(final ZipFile zip, final ZipEntry entry,
            final Charset charset)
    {
        if (entry.isDirectory())
        {
            throw new IllegalArgumentException("ZIP entry " + entry + " must not be a directory");
        }
        return new ZipEntryInput(zip, entry, charset);
    }

    BufferedReader createReader() throws IOException;

    String getPath();

    boolean isRealFile();

    Path toPath();
}
