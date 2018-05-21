package org.itsallcode.openfasttrace.importer.zip;

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

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hamcrest.Matchers;
import org.itsallcode.openfasttrace.importer.MultiFileImporter;
import org.itsallcode.openfasttrace.importer.input.InputFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.*;

public class ITZipFileImporter
{
    private static final String FILE_CONTENT_STRING = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";
    private static final byte[] FILE_CONTENT = FILE_CONTENT_STRING.getBytes(StandardCharsets.UTF_8);
    private static final String FILE_CONTENT2_STRING = "file content 2";
    private static final byte[] FILE_CONTENT2 = FILE_CONTENT2_STRING
            .getBytes(StandardCharsets.UTF_8);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    @Mock
    private MultiFileImporter delegateImporterMock;
    @Captor
    private ArgumentCaptor<InputFile> arg;

    private File zipFile;
    private ZipOutputStream zipOutputStream;
    private List<String> actualFileContent;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
        this.actualFileContent = new ArrayList<>();
        this.zipFile = this.tempFolder.newFile();
        this.zipOutputStream = new ZipOutputStream(new FileOutputStream(this.zipFile),
                StandardCharsets.UTF_8);
        when(this.delegateImporterMock.importFile(any())).thenAnswer(invocation -> {
            final InputFile inputFile = invocation.getArgument(0);
            this.actualFileContent.add(inputFile.createReader().lines().collect(joining("\n")));
            return null;
        });
    }

    @Test
    public void testImportEmptyZipDoesNothing() throws IOException
    {
        runImporter(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testImportNonRealFile() throws IOException
    {
        final InputFile file = InputFile.forReader(this.zipFile.toPath(),
                new BufferedReader(new StringReader("")));
        new ZipFileImporter(file, this.delegateImporterMock).runImport();
    }

    @Test
    public void testImportZipWithEmptyDirectoryDoesNothing() throws IOException
    {
        addZipEntryDirectory("dir");
        runImporter(0);
    }

    @Test
    public void testImportZipWithFileInRootDir() throws IOException
    {
        addEntryToZip("file", FILE_CONTENT);
        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!file"));
    }

    @Test
    public void testImportZipWithFileInSubDir() throws IOException
    {
        addEntryToZip("dir/file", FILE_CONTENT);
        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!dir/file"));
    }

    @Test
    public void testImportZipAssertFileContent() throws IOException
    {
        addEntryToZip("file", FILE_CONTENT);

        final List<InputFile> importedFiles = runImporter(1);
        assertThat(importedFiles.get(0).getPath(), equalTo(this.zipFile.getPath() + "!file"));
        assertThat(this.actualFileContent.get(0), equalTo(FILE_CONTENT_STRING));
    }

    @Test
    public void testImportZipWithMultipleFilesAssertFileContent() throws IOException
    {
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
        final InputFile file = InputFile.forPath(this.zipFile.toPath());
        new ZipFileImporter(file, this.delegateImporterMock).runImport();
        if (expectedFileCount == 0)
        {
            verifyZeroInteractions(this.delegateImporterMock);
            return emptyList();
        }
        verify(this.delegateImporterMock, times(expectedFileCount)).importFile(this.arg.capture());
        final List<InputFile> importedFiles = this.arg.getAllValues();
        assertThat(importedFiles, hasSize(expectedFileCount));
        assertThat(this.actualFileContent, hasSize(expectedFileCount));
        return importedFiles;
    }
}
