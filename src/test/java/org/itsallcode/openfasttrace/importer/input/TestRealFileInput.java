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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestRealFileInput
{
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testRelativeFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = InputFile.forPath(path);
        assertThat(inputFile.getPath(), equalTo("blah"));
        assertThat(inputFile.toString(), equalTo("blah"));
    }

    @Test
    public void testAbsoluteFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = InputFile.forPath(path);
        assertThat(inputFile.getPath(), equalTo(path.toString()));
        assertThat(inputFile.toString(), equalTo(path.toString()));
    }

    @Test
    public void testRelativeFileToPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = InputFile.forPath(path);
        assertThat(inputFile.toPath(), equalTo(path));
    }

    @Test
    public void testAbsoluteFileToPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = InputFile.forPath(path);
        assertThat(inputFile.toPath(), equalTo(path));
    }

    @Test
    public void testIsRealFileTrue() throws IOException
    {
        final InputFile inputFile = InputFile.forPath(Paths.get("blah"));
        assertThat(inputFile.isRealFile(), equalTo(true));
    }

    @Test
    public void testReadWithDefaultEncoding() throws IOException
    {
        final InputFile inputFile = InputFile
                .forPath(writeTempFile(CONTENT, StandardCharsets.UTF_8));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private String readContent(final InputFile inputFile) throws IOException
    {
        return inputFile.createReader().lines().collect(joining("\n"));
    }

    @Test
    public void testReadWithIsoEncoding() throws IOException
    {
        final InputFile inputFile = InputFile.forPath(
                writeTempFile(CONTENT, StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    @Test
    public void testReadWithUtf8Encoding() throws IOException
    {
        final InputFile inputFile = InputFile
                .forPath(writeTempFile(CONTENT, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private Path writeTempFile(final String content, final Charset charset) throws IOException
    {
        final Path path = this.tempFolder.newFile().toPath();
        Files.write(path, content.getBytes(charset));
        return path;
    }
}
