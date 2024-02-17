package org.itsallcode.openfasttrace.importer.zip;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.api.importer.MultiFileImporter;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ITZipFileImporter
{
    private static final String FILE_CONTENT_STRING = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";
    private static final byte[] FILE_CONTENT = FILE_CONTENT_STRING.getBytes(StandardCharsets.UTF_8);
    private static final String FILE_CONTENT2_STRING = "file content 2";
    private static final byte[] FILE_CONTENT2 = FILE_CONTENT2_STRING
            .getBytes(StandardCharsets.UTF_8);

    @Mock
    private MultiFileImporter delegateImporterMock;
    @Captor
    private ArgumentCaptor<InputFile> arg;

    private File zipFile;
    private ZipOutputStream zipOutputStream;
    private List<String> actualFileContent;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir)
    {
        this.actualFileContent = new ArrayList<>();
        this.zipFile = tempDir.resolve("test.zip").toFile();
        this.zipOutputStream = null;
        lenient().when(this.delegateImporterMock.importFile(any())).thenAnswer(invocation -> {
            final InputFile inputFile = invocation.getArgument(0);
            this.actualFileContent.add(inputFile.createReader().lines().collect(joining("\n")));
            return null;
        });
    }

    private void initializeZipFile() throws FileNotFoundException
    {
        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile),
                StandardCharsets.UTF_8);
    }

    @Test
    void testImportEmptyZipDoesNothing() throws IOException
    {
        initializeZipFile();
        runImporter(0);
    }

    @Test
    void testImportNonPhysicalFile()
    {
        final InputFile file = StreamInput.forReader(this.zipFile.toPath(),
                new BufferedReader(new StringReader("")));
        final ZipFileImporter importer = new ZipFileImporter(file, this.delegateImporterMock);
        assertThrows(UnsupportedOperationException.class,
                () -> importer.runImport());
    }

    @Test
    void testImportZipWithEmptyDirectoryDoesNothing() throws IOException
    {
        initializeZipFile();
        addZipEntryDirectory("dir");
        runImporter(0);
    }

    @Test
    void testImportZipWithFileInRootDir() throws IOException
    {
        initializeZipFile();
        addEntryToZip("file", FILE_CONTENT);
        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!file"));
    }

    @Test
    void testImportZipWithFileInSubDir() throws IOException
    {
        initializeZipFile();
        addEntryToZip("dir/file", FILE_CONTENT);
        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!dir/file"));
    }

    @Test
    void testImportZipAssertFileContent() throws IOException
    {
        initializeZipFile();
        addEntryToZip("file", FILE_CONTENT);

        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!file"));
        assertThat(this.actualFileContent.get(0), equalTo(FILE_CONTENT_STRING));
    }

    @Test
    void testImportZipWithMultipleFilesAssertFileContent() throws IOException
    {
        initializeZipFile();
        addEntryToZip("file1", FILE_CONTENT);
        addEntryToZip("dir/file2", FILE_CONTENT2);

        final List<InputFile> importedFiles = runImporter(2);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!file1"));
        assertThat(importedFiles.get(1).getPath(), equalTo(this.zipFile.getPath() + "!dir/file2"));
        assertThat(this.actualFileContent.get(0), equalTo(FILE_CONTENT_STRING));
        assertThat(this.actualFileContent.get(1), equalTo(FILE_CONTENT2_STRING));
    }

    private void addZipEntryDirectory(final String name) throws IOException
    {
        assertThat(name, not(Matchers.endsWith("/")));
        addEntryToZip(name + "/", null);
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

    private List<InputFile> runImporter(final int expectedFileCount) throws IOException
    {
        this.zipOutputStream.close();
        final InputFile file = RealFileInput.forPath(this.zipFile.toPath());
        new ZipFileImporter(file, this.delegateImporterMock).runImport();
        if (expectedFileCount == 0)
        {
            verifyNoInteractions(this.delegateImporterMock);
            return emptyList();
        }
        verify(this.delegateImporterMock, times(expectedFileCount)).importFile(this.arg.capture());
        final List<InputFile> importedFiles = this.arg.getAllValues();
        assertThat(importedFiles, hasSize(expectedFileCount));
        assertThat(this.actualFileContent, hasSize(expectedFileCount));
        return importedFiles;
    }
}
