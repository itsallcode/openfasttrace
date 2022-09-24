package org.itsallcode.openfasttrace.importer.zip.input;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

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
