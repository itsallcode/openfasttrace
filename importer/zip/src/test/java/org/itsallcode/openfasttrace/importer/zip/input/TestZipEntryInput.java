package org.itsallcode.openfasttrace.importer.zip.input;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.itsallcode.openfasttrace.api.importer.ImporterException;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestZipEntryInput
{
    private static final String TEST_ZIP = "test.zip";
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";
    private File zipFile;
    private ZipOutputStream zipOutputStream;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir) throws IOException
    {
        this.zipFile = tempDir.resolve(TEST_ZIP).toFile();
        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile),
                StandardCharsets.UTF_8);
    }

    @Test
    void testRelativeFileGetPath() throws IOException
    {
        try (final ZipFile zip = getZipFile())
        {
            final InputFile inputFile = ZipEntryInput.forZipEntry(zip, new ZipEntry("dir/file"));
            final String expectedName = this.zipFile.getPath() + "!dir/file";
            assertThat(inputFile.getPath(), equalTo(expectedName));
            assertThat(inputFile.toString(), equalTo(expectedName));
        }
    }

    private ZipFile getZipFile() throws IOException
    {
        this.zipOutputStream.close();
        return new ZipFile(this.zipFile);
    }

    @Test
    void testToPathUnsupportedThrowsException() throws IOException
    {
        try (final ZipFile zip = getZipFile())
        {
            final InputFile input = ZipEntryInput.forZipEntry(zip, new ZipEntry("file"));
            assertThrows(UnsupportedOperationException.class,
                    () -> input.toPath());
        }
    }

    @Test
    void testCreateZipEntryForDirNotSupportedThrowsException() throws IOException
    {
        try (final ZipFile zip = getZipFile())
        {
            final ZipEntry entry = new ZipEntry("dir/");
            assertThrows(IllegalArgumentException.class,
                    () -> ZipEntryInput.forZipEntry(zip, entry));
        }
    }

    @Test
    void testIsRealFileFalse() throws IOException
    {
        try (final ZipFile zip = getZipFile())
        {
            final InputFile file = ZipEntryInput.forZipEntry(zip, new ZipEntry("file"));
            assertThat(file.isRealFile(), equalTo(false));
        }
    }

    @Test
    void testReadContentEntryDoesNotExistThrowsException() throws IOException
    {
        try (final ZipFile zip = getZipFile())
        {
            final InputFile inputFile = ZipEntryInput.forZipEntry(zip, new ZipEntry("file"));
            assertThrows(ImporterException.class, () -> readContent(inputFile));
        }
    }

    @Test
    void testReadContentUtf8() throws IOException
    {
        addEntryToZip("file", CONTENT.getBytes(StandardCharsets.UTF_8));
        try (final ZipFile zip = getZipFile())
        {
            final InputFile inputFile = ZipEntryInput.forZipEntry(zip, new ZipEntry("file"));
            assertThat(readContent(inputFile), equalTo(CONTENT));
        }
    }

    @Test
    void testReadContentIso() throws IOException
    {
        addEntryToZip("file", CONTENT.getBytes(StandardCharsets.ISO_8859_1));
        try (final ZipFile zip = getZipFile())
        {
            final InputFile inputFile = ZipEntryInput.forZipEntry(zip, new ZipEntry("file"),
                    StandardCharsets.ISO_8859_1);
            assertThat(readContent(inputFile), equalTo(CONTENT));
        }
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
