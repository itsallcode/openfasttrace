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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.itsallcode.openfasttrace.importer.ImporterException;

/**
 * An {@link InputFile} for {@link ZipEntry} of a {@link ZipFile}.
 */
public class ZipEntryInput implements InputFile
{
    private final ZipFile zip;
    private final ZipEntry entry;
    private final Charset charset;

    private ZipEntryInput(final ZipFile zip, final ZipEntry entry, final Charset charset)
    {
        this.zip = zip;
        this.entry = entry;
        this.charset = charset;
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

    @Override
    public BufferedReader createReader() throws IOException
    {
        final InputStream inputStream = this.zip.getInputStream(this.entry);
        if (inputStream == null)
        {
            throw new ImporterException(
                    "Entry '" + this.entry + "' does not exist in zip file " + this.zip.getName());
        }
        return new BufferedReader(new InputStreamReader(inputStream, this.charset));
    }

    @Override
    public String getPath()
    {
        return this.zip.getName() + "!" + this.entry.getName();
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

    @Override
    public String toString()
    {
        return getPath();
    }
}
