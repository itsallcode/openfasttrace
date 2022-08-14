package org.itsallcode.openfasttrace.api.importer.input;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TestRealFileInput
{
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";
    private Path tempDir;

    @BeforeEach
    void beforeEach(@TempDir final Path tempDir)
    {
        this.tempDir = tempDir;
    }

    @Test
    void testRelativeFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = RealFileInput.forPath(path);
        assertThat(inputFile.getPath(), equalTo("blah"));
        assertThat(inputFile.toString(), equalTo("blah"));
    }

    @Test
    void testAbsoluteFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = RealFileInput.forPath(path);
        assertThat(inputFile.getPath(), equalTo(path.toString()));
        assertThat(inputFile.toString(), equalTo(path.toString()));
    }

    @Test
    void testRelativeFileToPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = RealFileInput.forPath(path);
        assertThat(inputFile.toPath(), equalTo(path));
    }

    @Test
    void testAbsoluteFileToPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = RealFileInput.forPath(path);
        assertThat(inputFile.toPath(), equalTo(path));
    }

    @Test
    void testIsRealFileTrue() throws IOException
    {
        final InputFile inputFile = RealFileInput.forPath(Paths.get("blah"));
        assertThat(inputFile.isRealFile(), equalTo(true));
    }

    @Test
    void testReadWithDefaultEncoding() throws IOException
    {
        final InputFile inputFile = RealFileInput
                .forPath(writeTempFile(CONTENT, StandardCharsets.UTF_8));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private String readContent(final InputFile inputFile) throws IOException
    {
        try (BufferedReader reader = inputFile.createReader())
        {
            return reader.lines().collect(joining("\n"));
        }
    }

    @Test
    void testReadWithIsoEncoding() throws IOException
    {
        final InputFile inputFile = RealFileInput.forPath(
                writeTempFile(CONTENT, StandardCharsets.ISO_8859_1), StandardCharsets.ISO_8859_1);
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    @Test
    void testReadWithUtf8Encoding() throws IOException
    {
        final InputFile inputFile = RealFileInput
                .forPath(writeTempFile(CONTENT, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private Path writeTempFile(final String content, final Charset charset) throws IOException
    {
        final Path path = this.tempDir.resolve("test");
        Files.write(path, content.getBytes(charset));
        return path;
    }
}
