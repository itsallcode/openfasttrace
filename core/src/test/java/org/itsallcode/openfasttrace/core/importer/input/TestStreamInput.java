package org.itsallcode.openfasttrace.core.importer.input;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.itsallcode.openfasttrace.api.importer.input.InputFile;
import org.itsallcode.openfasttrace.testutil.importer.input.StreamInput;
import org.junit.jupiter.api.Test;

class TestStreamInput
{
    private static final String CONTENT = "file content 1\nabcöäüßÖÄÜ!\"§$%&/()=?`´'#+*~-_,.;:<>|^°";

    @Test
    void testRelativeFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah");
        final InputFile inputFile = StreamInput.forReader(path, null);
        assertThat(inputFile.getPath(), equalTo("blah"));
        assertThat(inputFile.toString(), equalTo("blah"));
    }

    @Test
    void testAbsoluteFileGetPath() throws IOException
    {
        final Path path = Paths.get("blah").toAbsolutePath();
        final InputFile inputFile = StreamInput.forReader(path, null);
        assertThat(inputFile.getPath(), equalTo(path.toString()));
        assertThat(inputFile.toString(), equalTo(path.toString()));
    }

    @Test
    void testToPathUnsupported() throws IOException
    {
        final Path path = Paths.get("blah");
        assertThrows(UnsupportedOperationException.class,
                () -> StreamInput.forReader(path, null).toPath());
    }

    @Test
    void testIsRealFileFalse() throws IOException
    {
        final InputFile inputFile = StreamInput.forReader(Paths.get("blah"), null);
        assertThat(inputFile.isRealFile(), equalTo(false));
    }

    @Test
    void testReadContent() throws IOException
    {
        final InputFile inputFile = StreamInput.forReader(null,
                new BufferedReader(new StringReader(CONTENT)));
        assertThat(readContent(inputFile), equalTo(CONTENT));
    }

    private String readContent(final InputFile inputFile) throws IOException
    {
        return inputFile.createReader().lines().collect(joining("\n"));
    }
}
