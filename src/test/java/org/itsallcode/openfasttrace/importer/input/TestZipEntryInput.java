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

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

import org.itsallcode.openfasttrace.importer.ImporterException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.MockitoAnnotations;

public class TestZipEntryInput
{
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private File zipFile;
    private ZipOutputStream zipOutputStream;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        this.zipFile = this.tempFolder.newFile();
        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile),
                StandardCharsets.UTF_8);
    }

    @Test
    public void testRelativeFileGetPath() throws IOException
    {
        final InputFile inputFile = InputFile.forZipEntry(getZipFile(), new ZipEntry("dir/file"));
        final String expectedName = this.zipFile.getPath() + "!dir/file";
        assertThat(inputFile.getPath(), equalTo(expectedName));
        assertThat(inputFile.toString(), equalTo(expectedName));
    }

    private ZipFile getZipFile() throws ZipException, IOException
    {
        this.zipOutputStream.close();
        return new ZipFile(this.zipFile);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToPathUnsupported() throws IOException
    {
        InputFile.forZipEntry(getZipFile(), new ZipEntry("file")).toPath();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateZipEntryForDirNotSupported() throws IOException
    {
        InputFile.forZipEntry(getZipFile(), new ZipEntry("dir/"));
    }

    @Test
    public void testIsRealFileFalse() throws IOException
    {
        final InputFile file = InputFile.forZipEntry(getZipFile(), new ZipEntry("file"));
        assertThat(file.isRealFile(), equalTo(false));
    }

    @Test(expected = ImporterException.class)
    public void testReadContentEntryDoesNotExist() throws IOException
    {
        final InputFile inputFile = InputFile.forZipEntry(getZipFile(), new ZipEntry("file"));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    @Test
    public void testReadContentUtf8() throws IOException
    {
        addEntryToZip("file", CONTENT.getBytes(StandardCharsets.UTF_8));
        final InputFile inputFile = InputFile.forZipEntry(getZipFile(), new ZipEntry("file"));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    @Test
    public void testReadContentIso() throws IOException
    {
        addEntryToZip("file", CONTENT.getBytes(StandardCharsets.ISO_8859_1));
        final InputFile inputFile = InputFile.forZipEntry(getZipFile(), new ZipEntry("file"),
                StandardCharsets.ISO_8859_1);
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private String readContent(final InputFile inputFile) throws IOException
    {
        return inputFile.createReader().lines().collect(joining("\n"));
    }

    private void addEntryToZip(final String entryName, final byte[] data) throws IOException
    {
        this.zipOutputStream.putNextEntry(new ZipEntry(entryName));
        if (data != null)
        {
            this.zipOutputStream.write(data);
        }
        this.zipOutputStream.closeEntry();
    }
}
